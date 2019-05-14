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
    final String expected = "@prefix dct:   <http://purl.org/dc/terms/> .\n" +
        "@prefix tag:   <http://example.org/ns/tag#> .\n" +
        "\n" +
        "<tag:p#1>  a         tag:MarkupElement ;\n" +
        "        dct:hasPart  ( <tag:w#2> <tag:TEXT#4> <tag:w#5> ) .\n" +
        "\n" +
        "<tag:TEXT#6>  a  tag:TextNode ;\n" +
        "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#value>\n" +
        "                \"World\" .\n" +
        "\n" +
        "<tag:TEXT#4>  a  tag:TextNode ;\n" +
        "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#value>\n" +
        "                \" \" .\n" +
        "\n" +
        "tag:Document0  a           tag:Document ;\n" +
        "        tag:hasRootMarkup  <tag:xml#0> .\n" +
        "\n" +
        "<tag:xml#0>  a       tag:MarkupElement ;\n" +
        "        dct:hasPart  ( <tag:p#1> ) .\n" +
        "\n" +
        "<tag:w#2>  a         tag:MarkupElement ;\n" +
        "        dct:hasPart  ( <tag:TEXT#3> ) .\n" +
        "\n" +
        "<tag:w#5>  a         tag:MarkupElement ;\n" +
        "        dct:hasPart  ( <tag:TEXT#6> ) .\n" +
        "\n" +
        "<tag:TEXT#3>  a  tag:TextNode ;\n" +
        "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#value>\n" +
        "                \"Hello\" .\n";

    oe.writeAsTurtle(System.out);
    oe.writeAsRDFXML(System.out);
    oe.writeAsJSONLD(System.out);
    oe.writeAsTriples(System.out);
    assertThat(ttl).isEqualTo(expected);
  }

  @Test
  public void test2() {
    String xml = "<data xmlns=\"http://example.org\">\n" +
        "    <item>\n" +
        "        <name>Hello</name>\n" +
        "    </item>\n" +
        "    <item>\n" +
        "        <name>Hello</name>\n" +
        "    </item>\n" +
        "</data>";
    OntologyExtractor oe = new OntologyExtractor(xml);
    String ttl = oe.toString(oe::writeAsTurtle);
    final String expected = "\"@prefix dct:   <http://purl.org/dc/terms/> .\n" +
        "@prefix tag:   <http://example.org/ns/tag#> .\n" +
        "\n" +
        "tag:Document0  a           tag:Document ;\n" +
        "        tag:hasRootMarkup  <tag:data#0> .\n" +
        "\n" +
        "<tag:TEXT#3>  a  tag:TextNode ;\n" +
        "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#value>\n" +
        "                \"\\n        \" .\n" +
        "\n" +
        "<tag:TEXT#9>  a  tag:TextNode ;\n" +
        "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#value>\n" +
        "                \"\\n        \" .\n" +
        "\n" +
        "<tag:name#4>  a      tag:MarkupElement ;\n" +
        "        dct:hasPart  ( <tag:TEXT#5> ) .\n" +
        "\n" +
        "<tag:item#2>  a      tag:MarkupElement ;\n" +
        "        dct:hasPart  ( <tag:TEXT#3> <tag:name#4> <tag:TEXT#6> ) .\n" +
        "\n" +
        "<tag:item#8>  a      tag:MarkupElement ;\n" +
        "        dct:hasPart  ( <tag:TEXT#9> <tag:name#10> <tag:TEXT#12> ) .\n" +
        "\n" +
        "<tag:data#0>  a      tag:MarkupElement ;\n" +
        "        dct:hasPart  ( <tag:TEXT#1> <tag:item#2> <tag:TEXT#7> <tag:item#8> <tag:TEXT#13> ) .\n" +
        "\n" +
        "<tag:TEXT#1>  a  tag:TextNode ;\n" +
        "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#value>\n" +
        "                \"\\n    \" .\n" +
        "\n" +
        "<tag:TEXT#7>  a  tag:TextNode ;\n" +
        "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#value>\n" +
        "                \"\\n    \" .\n" +
        "\n" +
        "<tag:TEXT#6>  a  tag:TextNode ;\n" +
        "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#value>\n" +
        "                \"\\n    \" .\n" +
        "\n" +
        "<tag:TEXT#13>  a  tag:TextNode ;\n" +
        "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#value>\n" +
        "                \"\\n\" .\n" +
        "\n" +
        "<tag:TEXT#5>  a  tag:TextNode ;\n" +
        "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#value>\n" +
        "                \"Hello\" .\n" +
        "\n" +
        "<tag:name#10>  a     tag:MarkupElement ;\n" +
        "        dct:hasPart  ( <tag:TEXT#11> ) .\n" +
        "\n" +
        "<tag:TEXT#12>  a  tag:TextNode ;\n" +
        "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#value>\n" +
        "                \"\\n    \" .\n" +
        "\n" +
        "<tag:TEXT#11>  a  tag:TextNode ;\n" +
        "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#value>\n" +
        "                \"Hello\" .\n" +
        "\"";

    assertThat(ttl).isEqualTo(expected);
    oe.writeAsRDFXML(System.out);
    oe.writeAsJSONLD(System.out);
    oe.writeAsTriples(System.out);
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
