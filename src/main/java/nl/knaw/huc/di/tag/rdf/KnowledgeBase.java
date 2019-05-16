package nl.knaw.huc.di.tag.rdf;

import org.apache.jena.rdf.model.Model;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

public class KnowledgeBase {
  final Model model;

  KnowledgeBase(Model model) {
    this.model = model;
  }

  public void writeAsTurtle(PrintStream out) {
    model.write(out, "TURTLE");
  }

  public void writeAsJSONLD(PrintStream out) {
    model.write(out, "JSON-LD");
  }

  public void writeAsRDFXML(PrintStream out) {
    model.write(out, "RDF/XML-ABBREV");
  }

  public void writeAsTriples(PrintStream out) {
    model.write(out, "N-TRIPLES");
  }

  public String toString(Consumer<PrintStream> printStreamConsumer) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(os);
    printStreamConsumer.accept(ps);
    try {
      return os.toString("UTF8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

}
