package org.apache.manifoldcf.crawler.connectors.filesystemxml.persistence.solrcommandio;


import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.commands.SolrAddCommand;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 20/03/14
 * Time: 18:07
 * To change this template use File | Settings | File Templates.
 */
public interface SolrCommandFactory {
  //SolrDeleteCommand createSolrDeleteCommand();

  SolrAddCommand createSolrAddCommand();
}
