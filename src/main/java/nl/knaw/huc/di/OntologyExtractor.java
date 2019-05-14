package nl.knaw.huc.di;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DCTerms;
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
      Document document = parse(xml);
      model = ModelFactory.createDefaultModel();
      Element root = document.getDocumentElement();
      buildModel(model, root);

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
      CharacterData cd = (CharacterData) node;
      return model.createLiteral(cd.getData());
    }

    return null;
  }

  private String resourceURI(Element element) {
    return String.format("http://example.org/%s/%s%s", element.getTagName(), element.getTagName(), resourceCounter.getAndIncrement());
  }

}
