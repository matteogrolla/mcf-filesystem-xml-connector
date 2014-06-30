package org.apache.manifoldcf.crawler.connectors.filesystemxml.service.core.delegates;

import org.apache.manifoldcf.agents.interfaces.ServiceInterruption;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.interfaces.SpecificationNode;
import org.apache.manifoldcf.crawler.interfaces.DocumentSpecification;
import org.apache.manifoldcf.crawler.interfaces.IDocumentIdentifierStream;
import org.apache.manifoldcf.crawler.system.Logging;

import java.io.File;
import java.io.IOException;


/** Document identifier stream.
 */
public class IdentifierStream implements IDocumentIdentifierStream
{
  protected String[] ids = null;
  protected int currentIndex = 0;

  public IdentifierStream(DocumentSpecification spec)
      throws ManifoldCFException
  {
    try
    {
      // Walk the specification for the "startpoint" types.  Amalgamate these into a list of strings.
      // Presume that all roots are startpoint nodes
      int i = 0;
      int j = 0;
      while (i < spec.getChildCount())
      {
        SpecificationNode n = spec.getChild(i);
        if (n.getType().equals("startpoint"))
          j++;
        i++;
      }
      ids = new String[j];
      i = 0;
      j = 0;
      while (i < ids.length)
      {
        SpecificationNode n = spec.getChild(i);
        if (n.getType().equals("startpoint"))
        {
          // The id returned MUST be in canonical form!!!
          ids[j] = new File(n.getAttributeValue("path")).getCanonicalPath();
          if (Logging.connectors.isDebugEnabled())
          {
            Logging.connectors.debug("Seed = '"+ids[j]+"'");
          }
          j++;
        }
        i++;
      }
    }
    catch (IOException e)
    {
      throw new ManifoldCFException("Could not get a canonical path",e);
    }
  }

  /** Get the next identifier.
   *@return the next document identifier, or null if there are no more.
   */
  public String getNextIdentifier()
      throws ManifoldCFException, ServiceInterruption
  {
    if (currentIndex == ids.length)
      return null;
    return ids[currentIndex++];
  }

  /** Close the stream.
   */
  public void close()
      throws ManifoldCFException
  {
    ids = null;
  }

}