package org.apache.manifoldcf.crawler.connectors.filesystemxml.persistence.solrcommandio;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.commands.SolrAddCommand;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.commands.SolrCommand;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service("solrCommandReader")
public class SolrCommandReader {

  private Digester digester;
  @Resource(name = "solrCommandBeanFactory")
  SolrCommandFactory solrCommandFactory;

  public SolrCommandReader() {
  }

  private void init(){
    digester = new Digester();
    digester.setValidating(false);

    digester.addRule("add/doc", new Rule() {

      @Override
      public void begin(String namespace, String name, Attributes attributes) {
        SolrAddCommand command = solrCommandFactory.createSolrAddCommand();
        getDigester().push(command);
        //logger.trace("pushed Aggregator: " + name);
      }

      @Override
      public void end(String nspace, String name) {
        getDigester().pop();
        //logger.trace("popped Aggregator: " + name);
      }
    });
    digester.addSetProperties("add/doc");
    digester.addObjectCreate("add/doc/field", DummySolrField.class);
    digester.addSetProperties("add/doc/field");
    digester.addSetNext("add/doc/field", "setField");
    digester.addCallMethod("add/doc/field", "setTesto", 0);
    digester.addSetNext("add/doc", "addDocumentWrapper");

    /*
    MG temporarily removing deletes
    digester.addRule("delete/id", new Rule() {

      @Override
      public void begin(String namespace, String name, Attributes attributes) {
        SolrDeleteCommand command = solrCommandFactory.createSolrDeleteCommand();
        getDigester().push(command);
        //logger.trace("pushed Aggregator: " + name);
      }

      @Override
      public void end(String nspace, String name) {
        getDigester().pop();
        //logger.trace("popped Aggregator: " + name);
      }
    });
    digester.addCallMethod("delete/id", "setId", 0);
    digester.addSetNext("delete/id", "addDocumentWrapper");
    */
  }

  public List<SolrCommand> parse(File f) throws ManifoldCFException {
    checkParsePreconditions();
    init();
    try {
      digester.push(new SolrCommandsContainer());
      SolrCommandsContainer commandsContainer = (SolrCommandsContainer)digester.parse(new FileInputStream(f));
      return commandsContainer.getDocsList();
    } catch (IOException e) {
      throw new ManifoldCFException("Unable to open document" + f.getName(), e);
    } catch (SAXParseException e) {
      String message = "Unable to parse document" + f.getName() + "Parse Error at line " + e.getLineNumber() + " column " + e.getColumnNumber();
      throw new ManifoldCFException(message, e);
    } catch (SAXException e) {
      throw new ManifoldCFException("Error with Sax", e);
    }
  }

  private void checkParsePreconditions(){
    if (solrCommandFactory == null){
      throw new IllegalStateException("Uninitialized"+
        "\nsolrCommandFactory: "+solrCommandFactory);
    }
  }

  public void setSolrCommandFactory(SolrCommandFactory solrCommandFactory) {
    this.solrCommandFactory = solrCommandFactory;
  }
}
