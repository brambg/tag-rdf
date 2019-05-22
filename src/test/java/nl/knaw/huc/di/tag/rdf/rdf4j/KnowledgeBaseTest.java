package nl.knaw.huc.di.tag.rdf.rdf4j;

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

    System.out.println("#TURTLE");
    kb.writeAsTurtle(System.out);
    System.out.println();

    System.out.println("#RDF/XML");
    kb.writeAsRDFXML(System.out);
    System.out.println();

    System.out.println("#JSON-LD");
    kb.writeAsJSONLD(System.out);
    System.out.println();

    System.out.println("#TRIPLES");
    kb.writeAsTriples(System.out);
    System.out.println();

    System.out.println("#DOT");
    System.out.println(DotFactory.fromKnowledgeBase(kb));
    System.out.println();

    System.out.println("#XML");
    System.out.println(xml);
    System.out.println();

    //    assertThat(ttl).isEqualTo(expected);
  }

}
