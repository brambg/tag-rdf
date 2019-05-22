package nl.knaw.huc.di.tag.rdf.jena;

import org.apache.jena.rdf.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DotFactory {

  public static String fromKnowledgeBase(KnowledgeBase kb) {
    StringBuilder dotBuilder = new StringBuilder("digraph KnowledgeBase{\n")
        .append("//graph [rankdir=LR]\n") //
        .append("node [style=\"filled\";fillcolor=\"white\"]\n");
    StmtIterator stmtIterator = kb.model.listStatements();
    AtomicInteger nodeCounter = new AtomicInteger();
    AtomicInteger edgeCounter = new AtomicInteger();
    Map<Resource, Integer> resource2nodenum = new HashMap<>();
    Map<String, String> nsPrefixMap = kb.model.getNsPrefixMap();
    while (stmtIterator.hasNext()) {
      final Statement statement = stmtIterator.nextStatement();
//      System.out.println(statement);
      Resource resource = statement.getSubject();
      int objectNum = processResource(dotBuilder, nodeCounter, resource2nodenum, nsPrefixMap, resource);

      RDFNode object = statement.getObject();
      int subjectNum = 0;
      if (object.isLiteral()) {
        subjectNum = nodeCounter.getAndIncrement();
        dotBuilder.append("node").append(subjectNum).append(" [shape=box;color=green;label=\"").append(object.asLiteral().getString().replaceAll("\\s+"," ")).append("\"]\n");
      } else if (object.isResource()) {
        resource = object.asResource();
        subjectNum = processResource(dotBuilder, nodeCounter, resource2nodenum, nsPrefixMap, resource);
      }

      Property predicate = statement.getPredicate();
      int edgeNum = edgeCounter.getAndIncrement();
      String label = compactURI(predicate.getURI(), nsPrefixMap);
      dotBuilder.append("node").append(objectNum).append("->").append("node").append(subjectNum).append(" [label=\"").append(label).append("\"]\n");
    }
    dotBuilder.append("}");
    return dotBuilder.toString();
  }

  private static int processResource(final StringBuilder dotBuilder, final AtomicInteger nodeCounter, final Map<Resource, Integer> resource2nodenum, final Map<String, String> nsPrefixMap, final Resource resource) {
    final int subjectNum;
    if (resource2nodenum.containsKey(resource)) {
      subjectNum = resource2nodenum.get(resource);
    } else {
      subjectNum = nodeCounter.getAndIncrement();
      String label = label(resource, nsPrefixMap);
      dotBuilder.append("node").append(subjectNum).append(" [label=\"").append(label).append("\"]\n");
      resource2nodenum.put(resource, subjectNum);
    }
    return subjectNum;
  }

  private static String label(final Resource resource, final Map<String, String> nsPrefixMap) {
    return resource.isAnon() ? "" : compactURI(resource.getURI(), nsPrefixMap);
  }

  private static String compactURI(final String uri, final Map<String, String> nsPrefixMap) {
    for (String ns : nsPrefixMap.keySet()) {
      String prefix = nsPrefixMap.get(ns);
      if (uri.startsWith(prefix)) {
        return uri.replace(prefix, ns + ":");
      }
    }
    return uri;
  }
}
