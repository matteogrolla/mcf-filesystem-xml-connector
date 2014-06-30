package org.apache.manifoldcf.crawler.connectors.filesystemxml.domain;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.input.NullInputStream;
import org.apache.manifoldcf.agents.interfaces.RepositoryDocument;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

import java.io.*;
import java.util.Date;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 17/06/14
 * Time: 18:25
 * To change this template use File | Settings | File Templates.
 */
public class SerializableRepositoryDocument implements Serializable{
  private Date modifiedDate;
  private final String[] fieldNames;
  private final String[][] fieldValues;  //TODO: not all fields are String

  public SerializableRepositoryDocument(RepositoryDocument document) throws IOException {
    this.modifiedDate = document.getModifiedDate();

    int fieldsCount = document.fieldCount();
    fieldNames = new String[fieldsCount];
    fieldValues = new String[fieldsCount][];

    Iterator<String> fieldNamesIterator = document.getFields();
    int i=0;
    while (fieldNamesIterator.hasNext()){
      String fieldName = fieldNamesIterator.next();
      this.fieldNames[i] = fieldName;
      String[] fieldValues = document.getFieldAsStrings(fieldName);
      this.fieldValues[i++] = fieldValues;
    }
  }

  public RepositoryDocument toRepositoryDocument() throws ManifoldCFException {
    RepositoryDocument document = new RepositoryDocument();
    document.setModifiedDate(this.modifiedDate);
    document.setBinary(new NullInputStream(0), 0);  //TODO: document may have binary

    for (int i=0; i<fieldNames.length; i++){
      document.addField(fieldNames[i], fieldValues[i]);
    }
    return document;
  }

  public static String serializeDocument(RepositoryDocument document) throws ManifoldCFException {
    try{
      SerializableRepositoryDocument serializableDocument = new SerializableRepositoryDocument(document);
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      ObjectOutputStream so = new ObjectOutputStream(bo);
      so.writeObject(serializableDocument);
      so.flush();
      String serializedDocument = new String(Base64.encodeBase64(bo.toByteArray()));
      return serializedDocument;
    } catch (Exception e) {
      throw new ManifoldCFException("Can't serialize document ", e);  //TODO: log doc id
    }
  }

  public static RepositoryDocument deSerializeDocument(String serializedDocument) throws ManifoldCFException {
    try {
      byte b[] = Base64.decodeBase64(serializedDocument.getBytes());
      ByteArrayInputStream bi = new ByteArrayInputStream(b);
      ObjectInputStream si = new ObjectInputStream(bi);
      SerializableRepositoryDocument serializableDocument = (SerializableRepositoryDocument) si.readObject();
      RepositoryDocument document = serializableDocument.toRepositoryDocument();
      return document;
    } catch (Exception e) {
      throw new ManifoldCFException("Can't deserialize document ", e);  //TODO: log doc id
    }
  }
}
