package org.apache.manifoldcf.crawler.connectors.filesystemxml.service.core;

import org.apache.commons.io.input.NullInputStream;
import org.apache.manifoldcf.agents.interfaces.RepositoryDocument;
import org.apache.manifoldcf.agents.interfaces.ServiceInterruption;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.FileConnector;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.controller.ConnectorControllerImpl;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.DocumentSpecificationWrapper;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.FileSelectorCriteria;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.service.core.delegates.IdentifierStream;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.SerializableRepositoryDocument;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.commands.SolrAddCommand;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.commands.SolrCommand;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.persistence.solrcommandio.SolrCommandReader;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.service.utils.StringUtils;
import org.apache.manifoldcf.crawler.interfaces.DocumentSpecification;
import org.apache.manifoldcf.crawler.interfaces.IDocumentIdentifierStream;
import org.apache.manifoldcf.crawler.interfaces.IProcessActivity;
import org.apache.manifoldcf.crawler.interfaces.IVersionActivity;
import org.apache.manifoldcf.crawler.system.Logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 18/06/14
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
public class CrawlingServiceImpl implements CrawlingService {

  SolrCommandReader commandReader;
  DocumentsFilter documentsFilter;

  @Override
  public IDocumentIdentifierStream getDocumentIdentifiers(DocumentSpecification spec, long startTime, long endTime)
      throws ManifoldCFException {
    DocumentSpecificationWrapper dsw = new DocumentSpecificationWrapper(spec);
    List<FileSelectorCriteria> criterias = FileSelectorCriteria.deserializeFileSelectorCriterias(dsw.getFileSelectionValue());
    return new IdentifierStream(criterias);
  }

  @Override
  public String[] getDocumentVersions(String[] documentIdentifiers, String[] oldVersions, IVersionActivity activities,
                                      DocumentSpecification spec, int jobMode, boolean usesDefaultAuthority)
      throws ManifoldCFException, ServiceInterruption {
    int i = 0;

    String[] rval = new String[documentIdentifiers.length];
    i = 0;
    while (i < rval.length) {
      String documentIdentifier = documentIdentifiers[i];
      File file = new File(documentIdentifier);
      if (file.exists()) {
        if (file.isDirectory()) {
          // It's a directory.  The version ID will be the
          // last modified date.
          long lastModified = file.lastModified();
          rval[i] = new Long(lastModified).toString();
        } else {
          // It's a file
          long fileLength = file.length();
          if (activities.checkLengthIndexable(fileLength)) {
            // Get the file's modified date.
            long lastModified = file.lastModified();

            StringBuilder sb = new StringBuilder();

            sb.append("-");
            sb.append(new Long(lastModified).toString()).append(":").append(new Long(fileLength).toString());
            rval[i] = sb.toString();
          } else
            rval[i] = null;
        }
      } else {
        //it's a solr document
        /*
          FIXME versioning for documents in xml file
            If I understood correctly returning "" means that MCF will always process the document.
            If so then it's OK.
         */
        rval[i] = "";
      }
      i++;
    }
    return rval;
  }

  @Override
  public void processDocuments(String[] documentIdentifiers, String[] versions, IProcessActivity activities, DocumentSpecification spec, boolean[] scanOnly)
      throws ManifoldCFException, ServiceInterruption {

    DocumentSpecificationWrapper dsw = new DocumentSpecificationWrapper(spec);
    String fileSelectorValue = dsw.getFileSelectionValue();
    List<FileSelectorCriteria> fileSelectorCriterias = FileSelectorCriteria.deserializeFileSelectorCriterias(fileSelectorValue);

    int i = 0;
    while (i < documentIdentifiers.length) {
      String version = versions[i];
      String documentIdentifier = documentIdentifiers[i];
      File file = new File(documentIdentifier);
      if (file.exists() && file.isDirectory()) {
        processDirectory(documentIdentifier, file, activities, fileSelectorCriterias);
      } else {
        if (!scanOnly[i]) //MG what's scanOnly
        {
          // We've already avoided queuing documents that we don't want, based on file specifications.
          // We still need to check based on file data.
          if (file.exists()) {
            processFile(documentIdentifier, version, file, activities, spec);

          } else {
            processSolrDocument(documentIdentifier, version, activities);
          }
        }
      }
      i++;
    }
  }

  private void processDirectory(String documentIdentifier, File file, IProcessActivity activities, List<FileSelectorCriteria> fileSelectorCriterias) throws ManifoldCFException {
    // Queue up stuff for directory
    long startTime = System.currentTimeMillis();
    String errorCode = "OK";
    String errorDesc = null;
    String entityReference = documentIdentifier;
    try {
      try {
        File[] files = file.listFiles();
        if (files != null) {
          int j = 0;
          while (j < files.length) {
            File f = files[j++];
            String canonicalPath = f.getCanonicalPath();
            if (documentsFilter.checkInclude(f, canonicalPath, fileSelectorCriterias))
              activities.addDocumentReference(canonicalPath, documentIdentifier, FileConnector.RELATIONSHIP_CHILD);
          }
        }
      } catch (IOException e) {
        errorCode = "IO ERROR";
        errorDesc = e.getMessage();
        throw new ManifoldCFException("IO Error: " + e.getMessage(), e);
      }
    } finally {
      activities.recordActivity(new Long(startTime), FileConnector.ACTIVITY_READ, null, entityReference, errorCode, errorDesc, null);
    }
  }

  private void processFile(String documentIdentifier, String version, File file, IProcessActivity activities, DocumentSpecification spec) throws ManifoldCFException, ServiceInterruption {
    if (documentsFilter.checkIngest(file, spec)) {
                /*
                 * get filepathtouri value
                 */
      String convertPath = null;
      if (version.length() > 0 && version.startsWith("+")) {
        StringBuilder unpack = new StringBuilder();
        StringUtils.unpack(unpack, version, 1, '+');
        convertPath = unpack.toString();
      }

      long startTime = System.currentTimeMillis();
      String errorCode = "OK";
      String errorDesc = null;
      Long fileLength = null;
      String entityDescription = documentIdentifier;
      try {
        // Ingest the document.
        try {
          ingestFile(documentIdentifier, version, file, fileLength, convertPath, activities);

        } catch (FileNotFoundException e) {
          //skip. throw nothing.
          Logging.connectors.debug("Skipping file due to " + e.getMessage());
        } catch (IOException e) {
          errorCode = "IO ERROR";
          errorDesc = e.getMessage();
          throw new ManifoldCFException("IO Error: " + e.getMessage(), e);
        }
      } finally {
        activities.recordActivity(new Long(startTime), FileConnector.ACTIVITY_READ, fileLength, entityDescription, errorCode, errorDesc, null);
      }
    }
  }

  private void ingestFile(String documentIdentifier, String version, File file, Long fileLength, String convertPath, IProcessActivity activities) throws IOException, ManifoldCFException, ServiceInterruption {
    Logging.connectors.info("ingesting file: " + documentIdentifier);
    try {
      List<SolrCommand> solrCommandList = commandReader.parse(file);

      for (SolrCommand command : solrCommandList) {
        SolrAddCommand addCommand = (SolrAddCommand) command;
        RepositoryDocument document = addCommand.getDocument();

        String solrDocumentIdentifier = document.getFieldAsStrings("id")[0];
        document.setModifiedDate(new Date(file.lastModified()));
        document.setBinary(new NullInputStream(0), 0);

        /*
          FIXME: carrydown values
            serializing the documents seems a quick way to reach the goal
            what is the size limit for this data in the sql table?

            PROPOSAL
            What I'd really like is avoiding the use of a db table for carrydown data and keep them in memory
            something like:
              MCF starts processing File A
              docs A1, A2, ... AN are added to the queue
              MCF starts processing File B
              docs B1, B2, ... are added to the queue
              and so on...
              as soon as all docs A1..AN have been processed, A is considered processed
              in case of failure (manifold is restarted in the middle of a crawl)
                all files (A, B...) should be reprocessed
              the size of the queue should be bounded
                once filled MCF should stop processing files untill more docs are processed

            MOTIVATION
            -I'd like to avoid putting pressure on the db if possible, so that it doesn't become a concern in production
            -performance

          */
        String dataNames[] = {"data"};
        String dataValues[][] = {{SerializableRepositoryDocument.serializeDocument(document)}};

        String errorCode = "OK";
        String errorDesc = null;
        long startTime = System.currentTimeMillis();

        activities.addDocumentReference(solrDocumentIdentifier, documentIdentifier, FileConnector.RELATIONSHIP_CHILD, dataNames, dataValues);
        activities.recordActivity(new Long(startTime), FileConnector.ACTIVITY_READ, null, documentIdentifier, errorCode, errorDesc, null);

      }
    } catch (ManifoldCFException e){
      Logging.connectors.error("Can't ingest file "+documentIdentifier, e);
    }
  }

  private void processSolrDocument(String documentIdentifier, String version, IProcessActivity activities) throws ManifoldCFException, ServiceInterruption {
    long startTime = System.currentTimeMillis();
    String errorCode = "OK";
    String errorDesc = null;
    Long fileLength = null;
    String entityDescription = documentIdentifier;

    try {
      Logging.connectors.info("ingesting solr document: " + documentIdentifier);

      String[] dataValues = activities.retrieveParentData(documentIdentifier, "data");

      RepositoryDocument document = SerializableRepositoryDocument.deSerializeDocument(dataValues[0]);

      activities.ingestDocument(documentIdentifier, version, documentIdentifier, document);
    } finally {
      activities.recordActivity(new Long(startTime), FileConnector.ACTIVITY_READ, fileLength, entityDescription, errorCode, errorDesc, null);
    }
  }

  public void setCommandReader(SolrCommandReader commandReader) {
    this.commandReader = commandReader;
  }

  public void setDocumentsFilter(DocumentsFilter documentsFilter) {
    this.documentsFilter = documentsFilter;
  }
}
