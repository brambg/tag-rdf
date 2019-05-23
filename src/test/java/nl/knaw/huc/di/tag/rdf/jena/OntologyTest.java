package nl.knaw.huc.di.tag.rdf.jena;

import org.apache.jena.ext.xerces.util.URI;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class OntologyTest {
  @Test
  public void testTAGOntology() throws URI.MalformedURIException {
    Model model = ModelFactory.createDefaultModel()
        .setNsPrefix("rdf", RDF.getURI())
        .setNsPrefix("rdfs", RDFS.getURI())
//        .setNsPrefix("dc", DC.getURI())
        .setNsPrefix("tag", TAG.getURI());

    model.add(TAG.Document, RDF.type, RDFS.Class);
    addLabel(model, TAG.Document);
    model.add(TAG.Document, RDFS.comment, model.createLiteral("The document", "en"));
    addDefinedBy(model, TAG.Document);

    model.add(TAG.MarkupElement, RDF.type, RDFS.Class);
    addLabel(model, TAG.MarkupElement);
    model.add(TAG.MarkupElement, RDFS.comment, model.createLiteral("The markup", "en"));
    addDefinedBy(model, TAG.MarkupElement);

    model.add(TAG.TextNode, RDF.type, RDFS.Class);
    addLabel(model, TAG.TextNode);
    model.add(TAG.TextNode, RDFS.comment, model.createLiteral("The textnode", "en"));
    addDefinedBy(model, TAG.TextNode);

    model.add(TAG.Attribute, RDF.type, RDFS.Class);
    addLabel(model, TAG.Attribute);
    model.add(TAG.Attribute, RDFS.comment, model.createLiteral("The markup attribute", "en"));
    addDefinedBy(model, TAG.Attribute);

    model.add(TAG.hasAttributes, RDF.type, RDF.Property);
    addLabel(model, TAG.hasAttributes);
    model.add(TAG.hasElements, RDFS.comment, model.createLiteral("The (container of) attributes of this markupelement", "en"));
    model.add(TAG.hasAttributes, RDFS.domain, TAG.MarkupElement);
    model.add(TAG.hasAttributes, RDFS.range, RDFS.Container);
    addDefinedBy(model, TAG.hasAttributes);

    model.add(TAG.hasElements, RDF.type, RDF.Property);
    addLabel(model, TAG.hasElements);
    model.add(TAG.hasElements, RDFS.comment, model.createLiteral("The (container of) elements (markup, textnode) that this markupelement encloses", "en"));
    model.add(TAG.hasElements, RDFS.domain, TAG.MarkupElement);
    model.add(TAG.hasElements, RDFS.range, RDFS.Container);
    addDefinedBy(model, TAG.hasElements);

    model.add(TAG.hasRootMarkup, RDF.type, RDF.Property);
    addLabel(model, TAG.hasRootMarkup);
    model.add(TAG.hasRootMarkup, RDFS.comment, model.createLiteral("The root markup of this document", "en"));
    model.add(TAG.hasRootMarkup, RDFS.domain, TAG.Document);
    model.add(TAG.hasRootMarkup, RDFS.range, TAG.MarkupElement);
    addDefinedBy(model, TAG.hasRootMarkup);

    model.add(TAG.next, RDF.type, RDF.Property);
    addLabel(model, TAG.next);
    model.add(TAG.next, RDFS.comment, model.createLiteral("The textnode following this textnode", "en"));
    model.add(TAG.next, RDFS.domain, TAG.TextNode);
    model.add(TAG.next, RDFS.range, TAG.TextNode);
    addDefinedBy(model, TAG.next);

    model.add(TAG.name, RDF.type, RDF.Property);
    addLabel(model, TAG.name);
    model.add(TAG.name, RDFS.comment, model.createLiteral("The attribute name", "en"));
    model.add(TAG.name, RDFS.domain, TAG.Attribute);
    model.add(TAG.name, RDFS.range, RDFS.Literal);
    addDefinedBy(model, TAG.name);

    model.add(TAG.value, RDF.type, RDF.Property);
    addLabel(model, TAG.value);
    model.add(TAG.value, RDFS.comment, model.createLiteral("The attribute value", "en"));
    model.add(TAG.value, RDFS.domain, TAG.Attribute);
    model.add(TAG.value, RDFS.range, RDFS.Literal);
    addDefinedBy(model, TAG.value);

    model.write(System.out, "TURTLE");
  }

  private void addDefinedBy(final Model model, final Resource resource) {
    model.add(resource, RDFS.isDefinedBy, TAG.getURI());
  }

  private void addLabel(final Model model, final Resource resource) {
    model.add(resource, RDFS.label, model.createLiteral(resource.getLocalName(), "en"));
  }

  @Test
  public void testTAGMLOntology() throws FileNotFoundException {
    String namespace = "https://brambg.github.io/tag-rdf/tagml.rdf#";
    OntModel model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
    model.setNsPrefix("tag", namespace);

    OntClass document = model.createClass(namespace + "Document");
    addLabel(document, "Document");
    document.addSuperClass(RDFS.Resource);

    OntClass markupNode = model.createClass(namespace + "MarkupNode");
    addLabel(markupNode, "MarkupNode");
    markupNode.addSuperClass(RDFS.Resource);

    OntClass textNode = model.createClass(namespace + "TextNode");
    addLabel(textNode, "TextNode");
    textNode.addSuperClass(RDFS.Resource);

    OntClass annotationNode = model.createClass(namespace+ "AnnotationNode");
    addLabel(annotationNode, "AnnotationNode");
    annotationNode.addSuperClass(RDFS.Resource);

    OntProperty root = model.createOntProperty(namespace + "root");
    addLabel(root, "root");
    root.addDomain(document);
    root.addRange(markupNode);

    OntProperty annotation = model.createOntProperty(namespace + "annotation");
    addLabel(annotation, "annotation");
    annotation.addDomain(markupNode);
    annotation.addRange(annotationNode);

    OntProperty elements = model.createOntProperty(namespace + "elements");
    addLabel(elements, "elements");
    elements.addDomain(markupNode);
    elements.addRange(markupNode);
    elements.addRange(textNode);

    OntProperty name = model.createOntProperty(namespace + "name");
    addLabel(name, "name");
    name.addDomain(annotationNode);
    name.addRange(RDFS.Literal);

    OntProperty value = model.createOntProperty(namespace + "value");
    addLabel(value, "value");
    value.addDomain(annotationNode);
    value.addRange(RDFS.Literal);
    value.addRange(RDFS.Container);
    value.addRange(document);

    OntProperty next = model.createOntProperty(namespace + "next");
    addLabel(next, "next");
    next.addDomain(textNode);
    next.addRange(textNode);

    model.write(System.out, "TURTLE");
    model.write(new FileOutputStream(new File("out/tagml.rdf")));
    model.write(new FileOutputStream(new File("out/tagml.jsonld")),"JSONLD");
    model.write(new FileOutputStream(new File("out/tagml.ttl")),"TURTLE");
  }

  private void addLabel(final OntResource resource, final String label) {
    resource.addLabel(label, "en");
  }

}
