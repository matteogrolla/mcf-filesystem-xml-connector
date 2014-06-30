package org.apache.manifoldcf.crawler.connectors.filesystemxml.persistence.solrcommandio;

import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.commands.SolrAddCommand;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 12/06/14
 * Time: 18:28
 * To change this template use File | Settings | File Templates.
 */
public class SolrCommandFactoryImpl implements SolrCommandFactory {

  @Override
  public SolrAddCommand createSolrAddCommand() {
    return new SolrAddCommand();
  }

}
