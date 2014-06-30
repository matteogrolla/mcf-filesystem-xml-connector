package org.apache.manifoldcf.crawler.connectors.filesystemxml.service.utils;

import org.apache.manifoldcf.core.extmimemap.ExtensionMimeMap;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.interfaces.SpecificationNode;
import org.apache.manifoldcf.crawler.interfaces.DocumentSpecification;
import org.apache.manifoldcf.crawler.system.Logging;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 17/06/14
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

  /**
   * Convert a document identifier to a URI.  The URI is the URI that will be the unique key from
   * the search index, and will be presented to the user as part of the search results.
   *
   * @param path is the document filePath.
   * @return the document uri.
   */
  protected static String convertToWGETURI(String path)
      throws ManifoldCFException {
    //
    // Note well:  This MUST be a legal URI!!!
    try {
      StringBuffer sb = new StringBuffer();
      String[] tmp = path.split("/", 3);
      String scheme = "";
      String host = "";
      String other = "";
      if (tmp.length >= 1)
        scheme = tmp[0];
      else
        scheme = "http";
      if (tmp.length >= 2)
        host = tmp[1];
      else
        host = "localhost";
      if (tmp.length >= 3)
        other = "/" + tmp[2];
      else
        other = "/";
      return new URI(scheme + "://" + host + other).toURL().toString();
    } catch (java.net.MalformedURLException e) {
      throw new ManifoldCFException("Bad url: " + e.getMessage(), e);
    } catch (URISyntaxException e) {
      throw new ManifoldCFException("Bad url: " + e.getMessage(), e);
    }
  }

  /**
   * Convert a document identifier to a URI.  The URI is the URI that will be the unique key from
   * the search index, and will be presented to the user as part of the search results.
   *
   * @param documentIdentifier is the document identifier.
   * @return the document uri.
   */
  protected static String convertToURI(String documentIdentifier)
      throws ManifoldCFException {
    //
    // Note well:  This MUST be a legal URI!!!
    try {
      return new File(documentIdentifier).toURI().toURL().toString();
    } catch (IOException e) {
      throw new ManifoldCFException("Bad url", e);
    }
  }

  /**
   * Map an extension to a mime type
   */
  protected static String mapExtensionToMimeType(String fileName) {
    int slashIndex = fileName.lastIndexOf("/");
    if (slashIndex != -1)
      fileName = fileName.substring(slashIndex + 1);
    int dotIndex = fileName.lastIndexOf(".");
    if (dotIndex == -1)
      return null;
    return ExtensionMimeMap.mapToMimeType(fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT));
  }

}
