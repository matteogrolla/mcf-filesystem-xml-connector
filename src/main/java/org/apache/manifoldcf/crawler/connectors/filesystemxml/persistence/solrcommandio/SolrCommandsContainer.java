package org.apache.manifoldcf.crawler.connectors.filesystemxml.persistence.solrcommandio;


import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.commands.SolrCommand;

import java.util.LinkedList;
import java.util.List;

public class SolrCommandsContainer {

  private List<SolrCommand> docs;

  public SolrCommandsContainer() {
    this.docs = new LinkedList<SolrCommand>();
  }

  public SolrCommand getDocumentWrapper(int index) {
    return docs.get(index);
  }

  public List<SolrCommand> getDocsList() {
    return docs;
  }

  /* Methods called by Apache Digester */

  public void addDocumentWrapper(SolrCommand w) {
    this.docs.add(w);
  }

}
