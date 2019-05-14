package nl.knaw.huc.di;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

class OntologyExtractor {
  private Model model;
  private final AtomicInteger resourceCounter = new AtomicInteger();

  public OntologyExtractor(String xml) {
    try {
      Document xmlDoc = parse(xml);
      model = ModelFactory.createDefaultModel()
          .setNsPrefix("tag", TAG.NS)
          .setNsPrefix("dct", DCTerms.NS);
      Element root = xmlDoc.getDocumentElement();
      RDFNode rootNode = buildModel(model, root);

      model.createResource(TAG.Document.getURI() + "0")
          .addProperty(TAG.hasRootMarkup, rootNode)
          .addProperty(RDF.type, TAG.Document);

    } catch (ParserConfigurationException | IOException | SAXException e) {
      e.printStackTrace();
    }
  }

  public void writeAsTurtle(PrintStream out) {
    model.write(out, "TURTLE");
  }

  public void writeAsJSONLD(PrintStream out) {
    model.write(out, "JSON-LD");
  }

  public void writeAsRDFXML(PrintStream out) {
    model.write(out, "RDF/XML-ABBREV");
  }

  public void writeAsTriples(PrintStream out) {
    model.write(out, "N-TRIPLES");
  }

  public String toString(Consumer<PrintStream> printStreamConsumer) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(os);
    printStreamConsumer.accept(ps);
    try {
      return os.toString("UTF8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Document parse(final String xml) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    return builder.parse(input);
  }

  private RDFNode buildModel(Model model, Element element) {
    Resource resource = model.createResource(resourceURI(element));
    resource.addProperty(RDF.type, TAG.MarkupElement);
    NodeList childNodes = element.getChildNodes();
    int childlength = childNodes.getLength();
    List<RDFNode> nodes = new ArrayList<>();
    for (int i = 0; i < childlength; i++) {
      RDFNode childNode = toRDFNode(model, childNodes.item(i));
      if (childNode != null && childNode.isResource()) {
        nodes.add(childNode.asResource());
      } else if (childNode != null && childNode.isLiteral()) {
        nodes.add(childNode.asLiteral());
      }
    }
    RDFList list = model.createList(nodes.iterator());
    resource.addProperty(DCTerms.hasPart, list);
    return resource;
  }

  private RDFNode toRDFNode(Model model, Node node) {
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

    return null;
  }

  private String textResourceURI() {
    return String.format("tag:TEXT#%s", resourceCounter.getAndIncrement());
  }

  private String resourceURI(Element element) {
    return String.format("tag:%s#%s", element.getTagName(), resourceCounter.getAndIncrement());
  }

}
