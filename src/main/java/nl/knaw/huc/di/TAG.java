package nl.knaw.huc.di;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

class TAG {
  public static final String NS = "http://example.org/ns/tag#";
  public static final Resource Document = resource("Document");
  public static final Resource MarkupElement = resource("MarkupElement");
  public static final Resource TextNode = resource("TextNode");
  public static final Property hasText = property("hasText");
  public static final Property hasPart = property("hasPart");
  public static final Property hasRootMarkup = property("hasRootMarkup");

  public TAG() {
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
