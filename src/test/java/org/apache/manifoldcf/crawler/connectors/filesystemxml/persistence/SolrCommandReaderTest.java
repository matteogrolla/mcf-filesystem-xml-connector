package org.apache.manifoldcf.crawler.connectors.filesystemxml.persistence;

import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.commands.SolrCommand;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.persistence.solrcommandio.SolrCommandFactoryImpl;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.persistence.solrcommandio.SolrCommandReader;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 12/06/14
 * Time: 18:33
 * To change this template use File | Settings | File Templates.
 */
public class SolrCommandReaderTest {
  SolrCommandReader commandReader;

  @Before
  public void setup(){
    commandReader = new SolrCommandReader();
    commandReader.setSolrCommandFactory(new SolrCommandFactoryImpl());
  }

  @Test
  public void testParse() throws Exception {
    File xmlFile = new File(this.getClass().getResource("/dirsToIndex/addCommandsDir/hd.xml").getPath());

    List<SolrCommand> solrCommandList = commandReader.parse(xmlFile);
    int i=0;

  }
}
