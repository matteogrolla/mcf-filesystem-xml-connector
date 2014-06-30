package org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.commands;

import org.apache.log4j.Logger;
import org.apache.manifoldcf.agents.interfaces.RepositoryDocument;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.persistence.solrcommandio.DummySolrField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 13/03/14
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
@Component("solrAddCommand")
@Scope(value = "prototype")
public class SolrAddCommand extends SolrCommandBase {
  private static final Logger logger = Logger.getLogger(SolrAddCommand.class);

  protected RepositoryDocument document;


  public SolrAddCommand(){
    this.document = new RepositoryDocument();
  }

  public void execute() {

  }

  public RepositoryDocument getDocument(){
    return document;
  }

  /*
    following methods are used by Apache digester during xml unmarshalling
   */
  public void setBoost(float boost) {
  }

  public void setField(DummySolrField field) throws ManifoldCFException {
    document.addField(field.getName(), field.getTesto()); //, field.getBoost()
  }
}
