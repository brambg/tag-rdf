package nl.knaw.huc.di.tag.rdf.rdf4j;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

class TAG {
  public static final String NAMESPACE = "https://brambg.github.io/tag-rdf/tag.ttl#";
  public static final String PREFIX = "tag";

  public static final IRI Document;
  public static final IRI MarkupElement;
  public static final IRI TextNode;

  public static final IRI hasText;
  public static final IRI hasAttributes;
  public static final IRI hasElements;
  public static final IRI hasRootMarkup;
  public static final IRI next;
  public static final IRI name;
  public static final IRI value;

  private TAG() {
    throw new UnsupportedOperationException();
  }

  public static String getURI() {
    return NAMESPACE;
  }

  static {
    ValueFactory factory = SimpleValueFactory.getInstance();
    Document = factory.createIRI(NAMESPACE, "Document");
    MarkupElement = factory.createIRI(NAMESPACE, "MarkupElement");
    TextNode = factory.createIRI(NAMESPACE, "TextNode");
    hasText = factory.createIRI(NAMESPACE, "hasText");
    hasAttributes = factory.createIRI(NAMESPACE, "hasAttributes");
    hasElements = factory.createIRI(NAMESPACE, "hasElements");
    hasRootMarkup = factory.createIRI(NAMESPACE, "hasRootMarkup");
    next = factory.createIRI(NAMESPACE, "next");
    name = factory.createIRI(NAMESPACE, "name");
    value = factory.createIRI(NAMESPACE, "value");
  }
}
