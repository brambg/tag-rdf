package nl.knaw.huc.di;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Seq;
import org.apache.jena.rdf.model.impl.SeqImpl;
import org.apache.jena.vocabulary.DCTerms;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtractorTest {

  @Test
  public void test() {
    String xml = "<xml><p><w>Hello</w> <w>World</w></p></xml>";
    OntologyExtractor oe = new OntologyExtractor(xml);
    assertThat(oe).isNotNull();

  }

  @Test
  public void testJena() {
    // some definitions
    String xmlURI = "http://example.org/xml/xml01";
    String pURI = "http://example.org/p/p01";

// create an empty Model
    Model model = ModelFactory.createDefaultModel();

// create the resource
    Resource xml = model.createResource(xmlURI);
    Resource p = model.createResource(pURI);

// add the property
    xml.addProperty(DCTerms.hasPart, p);

    // now write the model in XML form to a file
    model.write(System.out);
    System.out.println();

    // now write the model in XML form to a file
    System.out.println("RDF/XML-ABBREV");
    model.write(System.out, "RDF/XML-ABBREV");
    System.out.println();


    // now write the model in N-TRIPLES form to a file
    System.out.println("N-TRIPLES");
    model.write(System.out, "N-TRIPLES");
    System.out.println();

    // now write the model in TURTLE form to a file
    System.out.println("TURTLE");
    model.write(System.out, "TURTLE");
    System.out.println();

    // now write the model in Json-LD form to a file
    System.out.println("JSON-LD");
    model.write(System.out, "JSONLD");
    System.out.println();
  }

  @Test
  public void testDOM() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    StringBuilder xmlStringBuilder = new StringBuilder();
    xmlStringBuilder.append("<xml><p><w>Hello</w> <w>World</w></p></xml>");
    ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
    Document document = builder.parse(input);

    Model model = ModelFactory.createDefaultModel();
    Element root = document.getDocumentElement();
    buildModel(model, root);

    model.write(System.out, "TURTLE");
  }

  private Resource buildModel(Model model, Element element) {
    Resource resource = model.createResource(resourceURI(element));
    NodeList childNodes = element.getChildNodes();
    int childlength = childNodes.getLength();
    for (int i = 0; i < childlength; i++) {
      Resource child = toResource(model, childNodes.item(i));
      if (child != null) {
        resource.addProperty(DCTerms.hasPart, child);
      }
      Seq seq = new SeqImpl();
    }
    return resource;
  }

  private Resource toResource(Model model, Node node) {
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      Element element = (Element) node;
      String uri = resourceURI(element);
      Resource resource = buildModel(model, element);
      return resource;

    }

    return null;
  }

  private String resourceURI(Element element) {
    return "http://example.org/" + element.getTagName();
  }


}
