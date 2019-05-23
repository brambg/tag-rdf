package nl.knaw.huc.di.tag.rdf.jena;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

class KnowledgeBaseFactory {

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
      Model model = ModelFactory.createDefaultModel()
          .setNsPrefix("rdf", RDF.getURI())
          .setNsPrefix("tag", TAG.NS);
      Element root = xmlDoc.getDocumentElement();
      RDFNode rootNode = buildModel(model, root, context);

      model.createResource(TAG.Document.getURI() + "0")
          .addProperty(TAG.hasRootMarkup, rootNode)
          .addProperty(RDF.type, TAG.Document);
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

  private static RDFNode buildModel(Model model, Element element, final Context context) {
    Resource resource = model.createResource(resourceURI(element, context))
        .addProperty(TAG.name, element.getTagName())
        .addProperty(RDF.type, TAG.MarkupElement);

    NodeList childNodes = element.getChildNodes();
    int childLength = childNodes.getLength();
    List<RDFNode> nodes = new ArrayList<>();
    for (int i = 0; i < childLength; i++) {
      RDFNode childNode = toRDFNode(model, childNodes.item(i), context);
      if (childNode != null && childNode.isResource()) {
        nodes.add(childNode.asResource());
      } else if (childNode != null && childNode.isLiteral()) {
        nodes.add(childNode.asLiteral());
      } else {
        System.out.println(childNode);
      }
    }
    if (!nodes.isEmpty()) {
      RDFList list = model.createList(nodes.iterator());
      resource.addProperty(TAG.hasElements, list);
    }

    NamedNodeMap attributes = element.getAttributes();
    int attributesLength = attributes.getLength();
    List<Resource> attributeResources = new ArrayList<>();
    for (int i = 0; i < attributesLength; i++) {
      Node node = attributes.item(i);
      String name = node.getNodeName();
      String nodeValue = node.getNodeValue();
      String annotationURI = attributeURI(context);
      Resource attributeResource = model.createResource(annotationURI)
          .addProperty(RDF.type, TAG.Attribute)
          .addProperty(TAG.name, name)
          .addProperty(TAG.value, nodeValue);
      attributeResources.add(attributeResource);
    }
    if (!attributeResources.isEmpty()) {
      RDFList list = model.createList(attributeResources.iterator());
      resource.addProperty(TAG.hasAttributes, list);
    }
    return resource;
  }

  private static RDFNode toRDFNode(Model model, Node node, final Context context) {
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      Element element = (Element) node;
      return buildModel(model, element, context);

    } else if (node.getNodeType() == Node.TEXT_NODE) {
      Resource textResource = model.createResource(textResourceURI(context))
          .addProperty(RDF.type, TAG.TextNode);
      if (context.lastTextResource != null) {
        context.lastTextResource.addProperty(TAG.next, textResource);
      }
      CharacterData cd = (CharacterData) node;
      Literal content = model.createLiteral(cd.getData());
      textResource.addProperty(RDF.value, content);
      context.lastTextResource = textResource;
      return textResource;
    }

    System.out.println("Unhandled nodeType " + node.getNodeType() + " " + node);
    return null;
  }

  private static String textResourceURI(final Context context) {
    return format("tag:_text#%s", context.resourceCounter.getAndIncrement());
  }

  private static String attributeURI(final Context context) {
    return format("tag:_attribute#%s", context.resourceCounter.getAndIncrement());
  }

  private static String resourceURI(Element element, final Context context) {
    return format("tag:%s#%s", element.getTagName(), context.resourceCounter.getAndIncrement());
  }

}
