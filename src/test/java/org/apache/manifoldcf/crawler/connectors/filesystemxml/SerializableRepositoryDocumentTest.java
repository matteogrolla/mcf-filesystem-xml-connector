package org.apache.manifoldcf.crawler.connectors.filesystemxml;

import org.apache.manifoldcf.agents.interfaces.RepositoryDocument;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.SerializableRepositoryDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 18/06/14
 * Time: 11:03
 * To change this template use File | Settings | File Templates.
 */
public class SerializableRepositoryDocumentTest {

  @Test
  public void testSerDeser() throws ManifoldCFException, IOException {
    RepositoryDocument document = new RepositoryDocument();
    document.addField("A","A1");
    document.setModifiedDate(new Date());

    String serializedDocument = SerializableRepositoryDocument.serializeDocument(document);

    RepositoryDocument doc = SerializableRepositoryDocument.deSerializeDocument(serializedDocument);

    assertEquals("A1", doc.getFieldAsStrings("A")[0]);
  }
}
