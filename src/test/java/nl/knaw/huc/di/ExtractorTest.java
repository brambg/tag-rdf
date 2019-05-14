package nl.knaw.huc.di;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtractorTest {
  @Test
  public void test() {
    String xml = "<xml><p><w>Hello</w> <w>World</w></p></xml>";
    OntologyExtractor oe = new OntologyExtractor(xml);
    String ttl = oe.toString(oe::writeAsTurtle);
    final String expected = "<http://example.org/w/w2>\n" +
        "        <http://purl.org/dc/terms/hasPart>\n" +
        "                ( \"Hello\" ) .\n" +
        "\n" +
        "<http://example.org/xml/xml0>\n" +
        "        <http://purl.org/dc/terms/hasPart>\n" +
        "                ( <http://example.org/p/p1> ) .\n" +
        "\n" +
        "<http://example.org/w/w3>\n" +
        "        <http://purl.org/dc/terms/hasPart>\n" +
        "                ( \"World\" ) .\n" +
        "\n" +
        "<http://example.org/p/p1>\n" +
        "        <http://purl.org/dc/terms/hasPart>\n" +
        "                ( <http://example.org/w/w2> \" \" <http://example.org/w/w3> ) .\n";
    System.out.println(ttl);
    assertThat(ttl).isEqualTo(expected);
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

}
