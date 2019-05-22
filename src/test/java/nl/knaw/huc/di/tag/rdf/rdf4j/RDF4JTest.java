package nl.knaw.huc.di.tag.rdf.rdf4j;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class RDF4JTest {

  @Test
  public void test1() {
    ValueFactory vf = SimpleValueFactory.getInstance();
    String ex = "http://example.org/";
    IRI picasso = vf.createIRI(ex, "Picasso");
    IRI artist = vf.createIRI(ex, "Artist");
    Model model = new TreeModel();
    model.add(picasso, RDF.TYPE, artist);
    model.add(picasso, FOAF.FIRST_NAME, vf.createLiteral("Pablo"));
    for (Statement statement : model) {
      System.out.println(statement);
    }
  }

  @Test
  public void test2() {
    ModelBuilder builder = new ModelBuilder();
    Model model = builder.setNamespace("ex", "http://example.org/")
        .subject("ex:PotatoEaters")
        // this painting was created in April 1885
        .add("ex:creationDate", new GregorianCalendar(1885, Calendar.APRIL, 1).getTime())
        // it depicts 5 people
        .add("ex:peopleDepicted", 5)
        .build();

// To see what's in our model, let's just print stuff to the screen
    for (Statement st : model) {
      // we want to see the object values of each property
      IRI property = st.getPredicate();
      Value value = st.getObject();
      if (value instanceof Literal) {
        Literal literal = (Literal) value;
        System.out.println("datatype: " + literal.getDatatype());

        // get the value of the literal directly as a Java primitive.
        if (property.getLocalName().equals("peopleDepicted")) {
          int peopleDepicted = literal.intValue();
          System.out.println(peopleDepicted + " people are depicted in this painting");
        } else if (property.getLocalName().equals("creationDate")) {
          XMLGregorianCalendar calendar = literal.calendarValue();
          Date date = calendar.toGregorianCalendar().getTime();
          System.out.println("The painting was created on " + date);
        }

        // You can also just get the lexical value (a string) without
        // worrying about the datatype
        System.out.println("Lexical value: '" + literal.getLabel() + "'");
      }
    }
    Rio.write(model, System.out, RDFFormat.TURTLE);
  }

}
