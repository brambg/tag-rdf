package nl.knaw.huc.di.tag.rdf;

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
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

class KnowledgeBaseFactory {
  private static final AtomicInteger resourceCounter = new AtomicInteger();

  private KnowledgeBaseFactory() {
  }

  public static KnowledgeBase fromXML(final String xml) {
    try {
      resourceCounter.set(0);
      Document xmlDoc = parse(xml);
      Model model = ModelFactory.createDefaultModel()
          .setNsPrefix("rdf", RDF.getURI())
          .setNsPrefix("tag", TAG.NS);
      Element root = xmlDoc.getDocumentElement();
      RDFNode rootNode = buildModel(model, root);

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

  private static RDFNode buildModel(Model model, Element element) {
    Resource resource = model.createResource(resourceURI(element));
    resource.addProperty(RDF.type, TAG.MarkupElement);

    NodeList childNodes = element.getChildNodes();
    int childLength = childNodes.getLength();
    final Seq seq = model.createSeq();
    for (int i = 0; i < childLength; i++) {
      RDFNode childNode = toRDFNode(model, childNodes.item(i));
      if (childNode != null && childNode.isResource()) {
        seq.add(childNode.asResource());
      } else if (childNode != null && childNode.isLiteral()) {
        seq.add(childNode.asLiteral());
      } else {
        System.out.println(childNode);
      }
    }
    resource.addProperty(TAG.hasElements, seq);

    NamedNodeMap attributes = element.getAttributes();
    int attributesLength = attributes.getLength();
    for (int i = 0; i < attributesLength; i++) {
      Node node = attributes.item(i);
      String name = node.getNodeName();
      String nodeValue = node.getNodeValue();
      String propertyURI = annotationURI(name);
      Property property = model.createProperty(TAG.NS, propertyURI);
      resource.addProperty(property, nodeValue);
    }

    return resource;
  }

  private static RDFNode toRDFNode(Model model, Node node) {
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      Element element = (Element) node;
      return buildModel(model, element);

    } else if (node.getNodeType() == Node.TEXT_NODE) {
      Resource textResource = model.createResource(textResourceURI());
      textResource.addProperty(RDF.type, TAG.TextNode);
      CharacterData cd = (CharacterData) node;
      Literal content = model.createLiteral(cd.getData());
      textResource.addProperty(RDF.value, content);
      return textResource;
    }

    System.out.println("Unhandled nodeType " + node.getNodeType() + " " + node);
    return null;
  }

  private static String textResourceURI() {
    return format("tag:TEXT#%s", resourceCounter.getAndIncrement());
  }

  private static String annotationURI(final String name) {
    return format("has_%s_attribute", name);
  }

  private static String resourceURI(Element element) {
    return format("tag:%s#%s", element.getTagName(), resourceCounter.getAndIncrement());
  }

}
