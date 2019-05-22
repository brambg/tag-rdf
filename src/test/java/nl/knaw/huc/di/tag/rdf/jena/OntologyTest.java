package nl.knaw.huc.di.tag.rdf.jena;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Test;

public class OntologyTest {
  @Test
  public void testTAGMLOntology() {
    Model model = ModelFactory.createDefaultModel()
        .setNsPrefix("rdf", RDF.getURI())
        .setNsPrefix("rdfs", RDFS.getURI())
        .setNsPrefix("tag", TAG.getURI());

    model.add(TAG.Document, RDF.type, RDFS.Class);
    model.add(TAG.MarkupElement, RDF.type, RDFS.Class);
    model.add(TAG.TextNode, RDF.type, RDFS.Class);

    model.add(TAG.hasAttributes, RDF.type, RDF.Property);
    model.add(TAG.hasAttributes, RDFS.domain, TAG.MarkupElement);
    model.add(TAG.hasAttributes, RDFS.range, RDFS.Container);

    model.add(TAG.hasElements, RDF.type, RDF.Property);
    model.add(TAG.hasElements, RDFS.domain, TAG.MarkupElement);
    model.add(TAG.hasElements, RDFS.range, RDFS.Container);

    model.add(TAG.hasText, RDF.type, RDF.Property);

    model.add(TAG.hasRootMarkup, RDF.type, RDF.Property);
    model.add(TAG.hasRootMarkup, RDFS.domain, TAG.Document);
    model.add(TAG.hasRootMarkup, RDFS.range, TAG.MarkupElement);

    model.add(TAG.next, RDF.type, RDF.Property);
    model.add(TAG.next, RDFS.domain, TAG.TextNode);
    model.add(TAG.next, RDFS.range, TAG.TextNode);

    model.add(TAG.name, RDF.type, RDF.Property);
    model.add(TAG.name, RDFS.domain, TAG.MarkupElement);
    model.add(TAG.name, RDFS.range, RDFS.Literal);

    model.add(TAG.value, RDF.type, RDF.Property);
    model.add(TAG.value, RDFS.domain, TAG.TextNode);
    model.add(TAG.value, RDFS.range, RDFS.Literal);

    model.write(System.out, "TURTLE");
  }

  @Test
  public void testOntology() {
    Model model = ModelFactory.createOntologyModel()
        .setNsPrefix("dc", DC.NS)
//        .setNsPrefix("tag", TAG.NS)
        ;
    Resource ontology = model.createResource(TAG.getURI())
        .addProperty(RDF.type, OWL.Ontology)
        .addProperty(DC.title, "The RDF Concepts Vocabulary (RDF)")
        .addProperty(DC.description, "This is the RDF Schema for the RDF vocabulary terms in the RDF Namespace, defined in RDF 1.1 Concepts.");

    model.write(System.out, "TURTLE");
  }

}
