package nl.knaw.huc.di.tag.rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.junit.Test;

public class KnowledgeBaseTest {
  @Test
  public void test() {
    String xml = "<xml><p xml:id=\"p01\" first=\"true\"><w type=\"greeting\">Hello</w> <w type=\"name\">World</w></p></xml>";
    KnowledgeBase kb = KnowledgeBaseFactory.fromXML(xml);
    String ttl = kb.toString(kb::writeAsTurtle);
    final String expected = "@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
        "@prefix tag:   <https://github.com/brambg/tag-rdf/tag.ttl#> .\n" +
        "\n" +
        "tag:Document0  a           tag:Document ;\n" +
        "        tag:hasRootMarkup  <tag:xml#0> .\n" +
        "\n" +
        "<tag:p#1>  a                      tag:MarkupElement ;\n" +
        "        tag:hasElements           [ a       rdf:Seq ;\n" +
        "                                    rdf:_1  <tag:w#2> ;\n" +
        "                                    rdf:_2  <tag:TEXT#4> ;\n" +
        "                                    rdf:_3  <tag:w#5>\n" +
        "                                  ] ;\n" +
        "        tag:has_first_attribute   \"true\" ;\n" +
        "        tag:has_xml:id_attribute  \"p01\" .\n" +
        "\n" +
        "<tag:TEXT#6>  a    tag:TextNode ;\n" +
        "        rdf:value  \"World\" .\n" +
        "\n" +
        "<tag:TEXT#4>  a    tag:TextNode ;\n" +
        "        rdf:value  \" \" .\n" +
        "\n" +
        "<tag:xml#0>  a           tag:MarkupElement ;\n" +
        "        tag:hasElements  [ a       rdf:Seq ;\n" +
        "                           rdf:_1  <tag:p#1>\n" +
        "                         ] .\n" +
        "\n" +
        "<tag:w#2>  a                    tag:MarkupElement ;\n" +
        "        tag:hasElements         [ a       rdf:Seq ;\n" +
        "                                  rdf:_1  <tag:TEXT#3>\n" +
        "                                ] ;\n" +
        "        tag:has_type_attribute  \"greeting\" .\n" +
        "\n" +
        "<tag:w#5>  a                    tag:MarkupElement ;\n" +
        "        tag:hasElements         [ a       rdf:Seq ;\n" +
        "                                  rdf:_1  <tag:TEXT#6>\n" +
        "                                ] ;\n" +
        "        tag:has_type_attribute  \"name\" .\n" +
        "\n" +
        "<tag:TEXT#3>  a    tag:TextNode ;\n" +
        "        rdf:value  \"Hello\" .\n";

    kb.writeAsTurtle(System.out);
    kb.writeAsRDFXML(System.out);
    System.out.println(DotFactory.fromKnowledgeBase(kb));
//    kb.writeAsJSONLD(System.out);
//    kb.writeAsTriples(System.out);
//    assertThat(ttl).isEqualTo(expected);
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
    KnowledgeBase kb = KnowledgeBaseFactory.fromXML(xml);
    String ttl = kb.toString(kb::writeAsTurtle);
    final String expected = "@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
        "@prefix tag:   <https://github.com/brambg/tag-rdf/tag.ttl#> .\n" +
        "\n" +
        "tag:Document0  a           tag:Document ;\n" +
        "        tag:hasRootMarkup  <tag:data#0> .\n" +
        "\n" +
        "<tag:TEXT#3>  a    tag:TextNode ;\n" +
        "        rdf:value  \"\\n        \" .\n" +
        "\n" +
        "<tag:TEXT#9>  a    tag:TextNode ;\n" +
        "        rdf:value  \"\\n        \" .\n" +
        "\n" +
        "<tag:name#4>  a          tag:MarkupElement ;\n" +
        "        tag:hasElements  [ a       rdf:Seq ;\n" +
        "                           rdf:_1  <tag:TEXT#5>\n" +
        "                         ] .\n" +
        "\n" +
        "<tag:item#2>  a          tag:MarkupElement ;\n" +
        "        tag:hasElements  [ a       rdf:Seq ;\n" +
        "                           rdf:_1  <tag:TEXT#3> ;\n" +
        "                           rdf:_2  <tag:name#4> ;\n" +
        "                           rdf:_3  <tag:TEXT#6>\n" +
        "                         ] .\n" +
        "\n" +
        "<tag:item#8>  a          tag:MarkupElement ;\n" +
        "        tag:hasElements  [ a       rdf:Seq ;\n" +
        "                           rdf:_1  <tag:TEXT#9> ;\n" +
        "                           rdf:_2  <tag:name#10> ;\n" +
        "                           rdf:_3  <tag:TEXT#12>\n" +
        "                         ] .\n" +
        "\n" +
        "<tag:data#0>  a                  tag:MarkupElement ;\n" +
        "        tag:hasElements          [ a       rdf:Seq ;\n" +
        "                                   rdf:_1  <tag:TEXT#1> ;\n" +
        "                                   rdf:_2  <tag:item#2> ;\n" +
        "                                   rdf:_3  <tag:TEXT#7> ;\n" +
        "                                   rdf:_4  <tag:item#8> ;\n" +
        "                                   rdf:_5  <tag:TEXT#13>\n" +
        "                                 ] ;\n" +
        "        tag:has_xmlns_attribute  \"http://example.org\" .\n" +
        "\n" +
        "<tag:TEXT#1>  a    tag:TextNode ;\n" +
        "        rdf:value  \"\\n    \" .\n" +
        "\n" +
        "<tag:TEXT#7>  a    tag:TextNode ;\n" +
        "        rdf:value  \"\\n    \" .\n" +
        "\n" +
        "<tag:TEXT#6>  a    tag:TextNode ;\n" +
        "        rdf:value  \"\\n    \" .\n" +
        "\n" +
        "<tag:TEXT#13>  a   tag:TextNode ;\n" +
        "        rdf:value  \"\\n\" .\n" +
        "\n" +
        "<tag:TEXT#5>  a    tag:TextNode ;\n" +
        "        rdf:value  \"Hello\" .\n" +
        "\n" +
        "<tag:name#10>  a         tag:MarkupElement ;\n" +
        "        tag:hasElements  [ a       rdf:Seq ;\n" +
        "                           rdf:_1  <tag:TEXT#11>\n" +
        "                         ] .\n" +
        "\n" +
        "<tag:TEXT#12>  a   tag:TextNode ;\n" +
        "        rdf:value  \"\\n    \" .\n" +
        "\n" +
        "<tag:TEXT#11>  a   tag:TextNode ;\n" +
        "        rdf:value  \"Hello\" .\n";

//    assertThat(ttl).isEqualTo(expected);
    System.out.println(DotFactory.fromKnowledgeBase(kb));
    kb.writeAsRDFXML(System.out);
    kb.writeAsJSONLD(System.out);
    kb.writeAsTriples(System.out);
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
