package org.apache.manifoldcf.crawler.connectors.filesystemxml.service.core;

import org.apache.manifoldcf.agents.interfaces.ServiceInterruption;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.crawler.interfaces.DocumentSpecification;
import org.apache.manifoldcf.crawler.interfaces.IDocumentIdentifierStream;
import org.apache.manifoldcf.crawler.interfaces.IProcessActivity;
import org.apache.manifoldcf.crawler.interfaces.IVersionActivity;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 18/06/14
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
public interface CrawlingService {
  /**
   * Given a document specification, get either a list of starting document identifiers (seeds),
   * or a list of changes (deltas), depending on whether this is a "crawled" connector or not.
   * These document identifiers will be loaded into the job's queue at the beginning of the
   * job's execution.
   * This method can return changes only (because it is provided a time range).  For full
   * recrawls, the start time is always zero.
   * Note that it is always ok to return MORE documents rather than less with this method.
   *
   * @param spec      is a document specification (that comes from the job).
   * @param startTime is the beginning of the time range to consider, inclusive.
   * @param endTime   is the end of the time range to consider, exclusive.
   * @return the stream of local document identifiers that should be added to the queue.
   */
  public IDocumentIdentifierStream getDocumentIdentifiers(DocumentSpecification spec, long startTime, long endTime)
      throws ManifoldCFException;

  /**
   * Get document versions given an array of document identifiers.
   * This method is called for EVERY document that is considered. It is therefore important to perform
   * as little work as possible here.
   * The connector will be connected before this method can be called.
   *
   * @param documentIdentifiers  is the array of local document identifiers, as understood by this connector.
   * @param oldVersions          is the corresponding array of version strings that have been saved for the document identifiers.
   *                             A null value indicates that this is a first-time fetch, while an empty string indicates that the previous document
   *                             had an empty version string.
   * @param activities           is the interface this method should use to perform whatever framework actions are desired.
   * @param spec                 is the current document specification for the current job.  If there is a dependency on this
   *                             specification, then the version string should include the pertinent data, so that reingestion will occur
   *                             when the specification changes.  This is primarily useful for metadata.
   * @param jobMode              is an integer describing how the job is being run, whether continuous or once-only.
   * @param usesDefaultAuthority will be true only if the authority in use for these documents is the default one.
   * @return the corresponding version strings, with null in the places where the document no longer exists.
   *         Empty version strings indicate that there is no versioning ability for the corresponding document, and the document
   *         will always be processed.
   */
  public String[] getDocumentVersions(String[] documentIdentifiers, String[] oldVersions, IVersionActivity activities,
                                      DocumentSpecification spec, int jobMode, boolean usesDefaultAuthority)
      throws ManifoldCFException, ServiceInterruption;

  /**
   * Process a set of documents.
   * This is the method that should cause each document to be fetched, processed, and the results either added
   * to the queue of documents for the current job, and/or entered into the incremental ingestion manager.
   * The document specification allows this class to filter what is done based on the job.
   *
   * @param documentIdentifiers is the set of document identifiers to process.
   * @param activities          is the interface this method should use to queue up new document references
   *                            and ingest documents.
   * @param spec                is the document specification.
   * @param scanOnly            is an array corresponding to the document identifiers.  It is set to true to indicate when the processing
   *                            should only find other references, and should not actually call the ingestion methods.
   */
  public void processDocuments(String[] documentIdentifiers, String[] versions, IProcessActivity activities, DocumentSpecification spec, boolean[] scanOnly)
      throws ManifoldCFException, ServiceInterruption;
}
