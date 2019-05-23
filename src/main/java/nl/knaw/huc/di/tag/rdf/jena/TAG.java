package nl.knaw.huc.di.tag.rdf.jena;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

class TAG {
  public static final String NS = "https://brambg.github.io/tag-rdf/tag.ttl#";

  public static final Resource Document = resource("Document");
  public static final Resource MarkupElement = resource("MarkupElement");
  public static final Resource TextNode = resource("TextNode");
  public static final Resource Attribute = resource("Attribute");

  public static final Property hasAttributes = property("hasAttributes");
  public static final Property hasElements = property("hasElements");
  public static final Property hasRootMarkup = property("hasRootMarkup");
  public static final Property next = property("next");
  public static final Property name = property("name");
  public static final Property value = property("value");

  private TAG() {
    throw new UnsupportedOperationException();
  }

  public static String getURI() {
    return NS;
  }

  private static Resource resource(String local) {
    return ResourceFactory.createResource(NS + local);
  }

  private static Property property(String local) {
    return ResourceFactory.createProperty(NS, local);
  }

}
