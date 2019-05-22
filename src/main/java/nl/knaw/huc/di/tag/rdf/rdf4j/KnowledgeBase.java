package nl.knaw.huc.di.tag.rdf.rdf4j;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

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
    Rio.write(model, out, RDFFormat.TURTLE);
  }

  public void writeAsJSONLD(PrintStream out) {
    Rio.write(model, out, RDFFormat.JSONLD);
  }

  public void writeAsRDFXML(PrintStream out) {
    Rio.write(model, out, RDFFormat.RDFXML);
  }

  public void writeAsTriples(PrintStream out) {
    Rio.write(model, out, RDFFormat.NTRIPLES);
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
