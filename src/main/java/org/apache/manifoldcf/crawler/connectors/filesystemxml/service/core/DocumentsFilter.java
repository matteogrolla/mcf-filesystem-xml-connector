package org.apache.manifoldcf.crawler.connectors.filesystemxml.service.core;

import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.FileSelectorCriteria;
import org.apache.manifoldcf.crawler.interfaces.DocumentSpecification;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 18/06/14
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
public interface DocumentsFilter {
  /**
   * Check if a file or directory should be included, given a document specification.
   *
   * @param fileName              is the canonical file name.
   * @param
   * @return true if it should be included.
   */
  public boolean checkInclude(File file, String fileName, List<FileSelectorCriteria> fileSelectorCriterias)
      throws ManifoldCFException;

  /**
   * Check if a file should be ingested, given a document specification.  It is presumed that
   * documents that do not pass checkInclude() will be checked with this method.
   *
   * @param file                  is the file.
   * @param documentSpecification is the specification.
   */
  public boolean checkIngest(File file, DocumentSpecification documentSpecification)
      throws ManifoldCFException;
}
