package nl.knaw.huc.di.tag.rdf.rdf4j;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

class KnowledgeBaseFactory {
  static ValueFactory vf = SimpleValueFactory.getInstance();

  private KnowledgeBaseFactory() {
  }

  private static class Context {
    final AtomicInteger resourceCounter = new AtomicInteger();
    Resource lastTextResource;
  }

  public static KnowledgeBase fromXML(final String xml) {
    try {
      Context context = new Context();
      Document xmlDoc = parse(xml);
      Model model = new LinkedHashModel();
      model.setNamespace(RDF.PREFIX, RDF.NAMESPACE);
      model.setNamespace(TAG.PREFIX, TAG.NAMESPACE);
      Element root = xmlDoc.getDocumentElement();
      IRI rootNode = buildModel(model, root, context);

      IRI documentIRI = vf.createIRI(TAG.Document.toString() + "0");
      model.add(documentIRI, TAG.hasRootMarkup, rootNode);
      model.add(documentIRI, RDF.TYPE, TAG.Document);
      return new KnowledgeBase(model);

    } catch (ParserConfigurationException | IOException | SAXException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private static Document parse(final String xml) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    return builder.parse(input);
  }

  private static IRI buildModel(Model model, Element element, final Context context) {
    IRI resource = asResourceIRI(element, context);
    model.add(resource, TAG.name, vf.createLiteral(element.getTagName()));

    NodeList childNodes = element.getChildNodes();
    int childLength = childNodes.getLength();
    Collection<Value> nodes = new ArrayList<>();
    for (int i = 0; i < childLength; i++) {
      Value childNode = toValue(model, childNodes.item(i), context);
      if (childNode != null) {
        nodes.add(childNode);
      }
    }
    if (!nodes.isEmpty()) {
      final BNode list = vf.createBNode();
      model = RDFCollections.asRDF(nodes, list, model);
      model.add(resource, TAG.hasElements, list);
    }

    NamedNodeMap attributes = element.getAttributes();
    int attributesLength = attributes.getLength();
    Collection<Value> attributeResources = new ArrayList<>();
    for (int i = 0; i < attributesLength; i++) {
      Node node = attributes.item(i);
      String name = node.getNodeName();
      String nodeValue = node.getNodeValue();
      IRI annotationIRI = annotationIRI(context);
      model.add(annotationIRI, TAG.name, vf.createLiteral(name));
      model.add(annotationIRI, TAG.value, vf.createLiteral(nodeValue));
      attributeResources.add(annotationIRI);
    }
    if (!attributeResources.isEmpty()) {
      final BNode list = vf.createBNode();
      model = RDFCollections.asRDF(attributeResources, list, model);
      model.add(resource, TAG.hasAttributes, list);
    }
    return resource;
  }

  private static IRI toValue(Model model, Node node, final Context context) {
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      Element element = (Element) node;
      return buildModel(model, element, context);

    } else if (node.getNodeType() == Node.TEXT_NODE) {
      IRI textResource = textResourceIRI(context);
      model.add(textResource, RDF.TYPE, TAG.TextNode);
      if (context.lastTextResource != null) {
        model.add(context.lastTextResource, TAG.next, textResource);
      }
      CharacterData cd = (CharacterData) node;
      Literal content = vf.createLiteral(cd.getData());
      model.add(textResource, RDF.VALUE, content);

      context.lastTextResource = textResource;
      return textResource;
    }

    System.out.println("Unhandled nodeType " + node.getNodeType() + " " + node);
    return null;
  }

  private static IRI textResourceIRI(final Context context) {
    return makeIRI("_text", context);
  }

  private static IRI annotationIRI(final Context context) {
    return makeIRI("_attribute", context);
  }

  private static IRI asResourceIRI(Element element, final Context context) {
    return makeIRI(element.getTagName(), context);
  }

  private static IRI makeIRI(final String baseName, final Context context) {
    String localName = format("%s_%s", baseName, context.resourceCounter.getAndIncrement());
    return vf.createIRI(TAG.NAMESPACE, localName);
  }

}
