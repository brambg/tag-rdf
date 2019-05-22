package nl.knaw.huc.di.tag.rdf.rdf4j;

import org.eclipse.rdf4j.model.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DotFactory {

  public static String fromKnowledgeBase(KnowledgeBase kb) {
    StringBuilder dotBuilder = new StringBuilder("digraph KnowledgeBase{\n")
        .append("//graph [rankdir=LR]\n") //
        .append("node [style=\"filled\";fillcolor=\"white\"]\n");
    Iterator<Statement> stmtIterator = kb.model.iterator();
    AtomicInteger nodeCounter = new AtomicInteger();
    Map<Resource, Integer> resource2nodenum = new HashMap<>();
    Set<Namespace> namespaces = kb.model.getNamespaces();
    while (stmtIterator.hasNext()) {
      final Statement statement = stmtIterator.next();
//      System.out.println(statement);
      Resource resource = statement.getSubject();
      int objectNum = processResource(dotBuilder, nodeCounter, resource2nodenum, namespaces, resource);

      final Value object = statement.getObject();
      int subjectNum = 0;
      if (object instanceof Literal) {
        subjectNum = nodeCounter.getAndIncrement();
        String label = object.stringValue().replaceAll("\\s+", " ");
        dotBuilder.append("node").append(subjectNum).append(" [shape=box;color=green;label=\"").append(label).append("\"]\n");
      } else if (object instanceof Resource) {
        resource = (Resource) object;
        subjectNum = processResource(dotBuilder, nodeCounter, resource2nodenum, namespaces, resource);
      }

      final IRI predicate = statement.getPredicate();
      String label = compactURI(predicate, namespaces);
      dotBuilder.append("node").append(objectNum).append("->").append("node").append(subjectNum).append(" [label=\"").append(label).append("\"]\n");
    }
    dotBuilder.append("}");
    return dotBuilder.toString();
  }

  private static int processResource(final StringBuilder dotBuilder, final AtomicInteger nodeCounter, final Map<Resource, Integer> resource2nodenum, final Set<Namespace> namespaces, final Resource resource) {
    final int subjectNum;
    if (resource2nodenum.containsKey(resource)) {
      subjectNum = resource2nodenum.get(resource);
    } else {
      subjectNum = nodeCounter.getAndIncrement();
      String label = label(resource, namespaces);
      dotBuilder.append("node").append(subjectNum).append(" [label=\"").append(label).append("\"]\n");
      resource2nodenum.put(resource, subjectNum);
    }
    return subjectNum;
  }

  private static String label(final Resource resource, final Set<Namespace> namespaces) {
    return resource instanceof BNode ? "" : compactURI((IRI) resource, namespaces);
  }

  private static String compactURI(final IRI iri, final Set<Namespace> namespaces) {
    String iriNamespace = iri.getNamespace();
    return namespaces.stream()
        .filter(ns -> ns.getName().equals(iriNamespace))
        .map(ns -> iri.toString().replace(iriNamespace, ns.getPrefix() + ":"))
        .findFirst()
        .orElse("");
  }
}
