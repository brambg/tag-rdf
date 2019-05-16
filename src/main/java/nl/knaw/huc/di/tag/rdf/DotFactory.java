package nl.knaw.huc.di.tag.rdf;

import org.apache.jena.rdf.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DotFactory {

  public static String fromKnowledgeBase(KnowledgeBase kb) {
    StringBuilder dotBuilder = new StringBuilder("digraph KnowledgeBase{\n")
        .append("graph [rankdir=LR]\n") //
        .append("node [style=\"filled\";fillcolor=\"white\"]\n");
    StmtIterator stmtIterator = kb.model.listStatements();
    AtomicInteger nodeCounter = new AtomicInteger();
    AtomicInteger edgeCounter = new AtomicInteger();
    Map<String, Integer> uri2nodenum = new HashMap<>();
    while (stmtIterator.hasNext()) {
      final Statement statement = stmtIterator.nextStatement();
      System.out.println(statement);
      Resource resource = statement.getSubject();
      String uri = resource.getURI();
      int objectNum = 0;
      if (uri2nodenum.containsKey(uri)) {
        objectNum = uri2nodenum.get(uri);
      } else {
        objectNum = nodeCounter.getAndIncrement();
        dotBuilder.append("node").append(objectNum).append(" [label=\"" + uri + "\"]\n");
        uri2nodenum.put(uri,objectNum);
      }

      RDFNode object = statement.getObject();
      int subjectNum = 0;
      if (object.isLiteral()) {
        subjectNum = nodeCounter.getAndIncrement();
        dotBuilder.append("node").append(subjectNum).append(" [label=\"" + object.asLiteral().getString() + "\"]\n");
      } else if (object.isResource()) {
        uri = object.asResource().getURI();
        if (uri2nodenum.containsKey(uri)) {
          subjectNum = uri2nodenum.get(uri);
        } else {
          subjectNum = nodeCounter.getAndIncrement();
          dotBuilder.append("node").append(subjectNum).append(" [label=\"" + uri + "\"]\n");
          uri2nodenum.put(uri,subjectNum);
        }
      }

      Property predicate = statement.getPredicate();
      int edgeNum = edgeCounter.getAndIncrement();
      dotBuilder.append("node").append(objectNum).append("->").append("node").append(subjectNum).append(" [label=\"").append(predicate.getNameSpace()).append(predicate.getLocalName()).append("\"]\n");
    }
    dotBuilder.append("}");
    return dotBuilder.toString();
  }
}
