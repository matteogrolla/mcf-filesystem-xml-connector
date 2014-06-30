package org.apache.manifoldcf.crawler.connectors.filesystemxml.service.core;

import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.interfaces.SpecificationNode;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.FileSelectorCriteria;
import org.apache.manifoldcf.crawler.interfaces.DocumentSpecification;
import org.apache.manifoldcf.crawler.system.Logging;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 18/06/14
 * Time: 15:13
 * To change this template use File | Settings | File Templates.
 */
public class DocumentsFilterImpl implements  DocumentsFilter {

  public boolean checkInclude(File file, String fileName, List<FileSelectorCriteria> fileSelectorCriterias)
      throws ManifoldCFException {
    if (Logging.connectors.isDebugEnabled()) {
      Logging.connectors.debug("Checking whether to include file '" + fileName + "'");
    }

    try {
      String pathPart;
      String filePart;
      if (file.isDirectory()) {
        pathPart = fileName;
        filePart = null;
      } else {
        pathPart = file.getParentFile().getCanonicalPath();
        filePart = file.getName();
      }

      // Scan until we match a startpoint
      int i = 0;
      for (FileSelectorCriteria criteria: fileSelectorCriterias){
        String path = new File(criteria.path).getCanonicalPath();
        if (Logging.connectors.isDebugEnabled()) {
          Logging.connectors.debug("Checking path '" + path + "' against canonical '" + pathPart + "'");
        }
        // Compare with filename
        int matchEnd = matchSubPath(path, pathPart);
        if (matchEnd == -1) {
          if (Logging.connectors.isDebugEnabled()) {
            Logging.connectors.debug("Match check '" + path + "' against canonical '" + pathPart + "' failed");
          }

          continue;
        }
        for (FileSelectorCriteria.Filter filter: criteria.filters){
          // If type is "file", then our match string is against the filePart.
          // If filePart is null, then this rule is simply skipped.
          String sourceMatch;
          int sourceIndex;
          if (filter.object == FileSelectorCriteria.Filter.FilterObject.FILE
              && file.isDirectory()){
            sourceMatch = filePart;
            sourceIndex = 0;
          } else if (filter.object == FileSelectorCriteria.Filter.FilterObject.DIRECTORY
              && !file.isDirectory()){
            sourceMatch = pathPart;
            sourceIndex = matchEnd;
          } else {
            continue;
          }

          if (filter.action == FileSelectorCriteria.Filter.Action.INCLUDE){
            if (checkMatch(sourceMatch,sourceIndex,filter.match))
              return true;
          }
          if (filter.action == FileSelectorCriteria.Filter.Action.EXCLUDE){
            if (checkMatch(sourceMatch,sourceIndex,filter.match))
              return false;
          }
        }
      }
      if (Logging.connectors.isDebugEnabled())
      {
        Logging.connectors.debug("Not including '"+fileName+"' because no matching rules");
      }

      return false;
    } catch (IOException e) {
      throw new ManifoldCFException("IO Error", e);
    }
  }


  public boolean checkIngest(File file, DocumentSpecification documentSpecification)
      throws ManifoldCFException {
    // Since the only exclusions at this point are not based on file contents, this is a no-op.
    // MHL
    return true;
  }

  /**
   * Match a sub-path.  The sub-path must match the complete starting part of the full path, in a path
   * sense.  The returned value should point into the file name beyond the end of the matched path, or
   * be -1 if there is no match.
   *
   * @param subPath  is the sub path.
   * @param fullPath is the full path.
   * @return the index of the start of the remaining part of the full path, or -1.
   */
  private static int matchSubPath(String subPath, String fullPath) {
    if (subPath.length() > fullPath.length())
      return -1;
    if (fullPath.startsWith(subPath) == false)
      return -1;
    int rval = subPath.length();
    if (fullPath.length() == rval)
      return rval;
    char x = fullPath.charAt(rval);
    if (x == File.separatorChar)
      rval++;
    return rval;
  }

  /**
   * Check a match between two strings with wildcards.
   *
   * @param sourceMatch is the expanded string (no wildcards)
   * @param sourceIndex is the starting point in the expanded string.
   * @param match       is the wildcard-based string.
   * @return true if there is a match.
   */
  private static boolean checkMatch(String sourceMatch, int sourceIndex, String match) {
    // Note: The java regex stuff looks pretty heavyweight for this purpose.
    // I've opted to try and do a simple recursive version myself, which is not compiled.
    // Basically, the match proceeds by recursive descent through the string, so that all *'s cause
    // recursion.
    boolean caseSensitive = true;

    return processCheck(caseSensitive, sourceMatch, sourceIndex, match, 0);
  }

  /**
   * Recursive worker method for checkMatch.  Returns 'true' if there is a path that consumes both
   * strings in their entirety in a matched way.
   *
   * @param caseSensitive is true if file names are case sensitive.
   * @param sourceMatch   is the source string (w/o wildcards)
   * @param sourceIndex   is the current point in the source string.
   * @param match         is the match string (w/wildcards)
   * @param matchIndex    is the current point in the match string.
   * @return true if there is a match.
   */
  private static boolean processCheck(boolean caseSensitive, String sourceMatch, int sourceIndex,
                                        String match, int matchIndex) {
    // Logging.connectors.debug("Matching '"+sourceMatch+"' position "+Integer.toString(sourceIndex)+
    //      " against '"+match+"' position "+Integer.toString(matchIndex));

    // Match up through the next * we encounter
    while (true) {
      // If we've reached the end, it's a match.
      if (sourceMatch.length() == sourceIndex && match.length() == matchIndex)
        return true;
      // If one has reached the end but the other hasn't, no match
      if (match.length() == matchIndex)
        return false;
      if (sourceMatch.length() == sourceIndex) {
        if (match.charAt(matchIndex) != '*')
          return false;
        matchIndex++;
        continue;
      }
      char x = sourceMatch.charAt(sourceIndex);
      char y = match.charAt(matchIndex);
      if (!caseSensitive) {
        if (x >= 'A' && x <= 'Z')
          x -= 'A' - 'a';
        if (y >= 'A' && y <= 'Z')
          y -= 'A' - 'a';
      }
      if (y == '*') {
        // Wildcard!
        // We will recurse at this point.
        // Basically, we want to combine the results for leaving the "*" in the match string
        // at this point and advancing the source index, with skipping the "*" and leaving the source
        // string alone.
        return processCheck(caseSensitive, sourceMatch, sourceIndex + 1, match, matchIndex) ||
            processCheck(caseSensitive, sourceMatch, sourceIndex, match, matchIndex + 1);
      }
      if (y == '?' || x == y) {
        sourceIndex++;
        matchIndex++;
      } else
        return false;
    }
  }
}
