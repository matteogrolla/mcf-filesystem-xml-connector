package org.apache.manifoldcf.crawler.connectors.filesystemxml.persistence.solrcommandio;

import com.sourcesense.sole24ore.curly.CurlyIndexerException;
import com.sourcesense.sole24ore.curly.CurlyLibraryException;
import com.sourcesense.sole24ore.curly.domain.IndexingStats;
import com.sourcesense.sole24ore.curly.domain.commands.SolrCommandsContainer;
import com.sourcesense.sole24ore.curly.domain.commands.SolrCommandsInFile;
import org.apache.log4j.Logger;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 31/03/14
 * Time: 10:30
 * To change this template use File | Settings | File Templates.
 */
@Service("solrCommandFileCrawler")
public class SolrCommandFileCrawler {
  private static Logger logger = Logger.getLogger(SolrCommandFileCrawler.class);

  List<File> listFiles;
  int currFileIndex = 0;

  @Autowired
  private IndexingStats stats;
  @Resource(name = "solrCommandReader")
  private SolrCommandReader solrCommandReader;

  /**
    Generate a list of Commands from files in a directory.
    Commands will be ordered in ascending order on their filename property
    to iterate the commands list use getNextCommand()
    @param dir  the folder where files must be searched
    @param date only files modified after this date will go in the list
   */
  public void crawlDirectoryByDate(File dir, Date date) throws ManifoldCFException {
    try {

      if (!dir.isDirectory()) {
        throw new ManifoldCFException("The dir provided is not valid");
      }
      /*
        file path in SolrCommandInFile are absolute
         to compare them with file in this directory, file obtain by this directory should have absolute path
         easiest way is to force the directory to have an absolute path
       */
      if (!dir.isAbsolute()) {
        dir = dir.getAbsoluteFile();
      }
      logger.info("Start analyzing directory: " + dir.getPath());
      listFiles = Arrays.asList(dir.listFiles());

      Collections.sort(listFiles, new Comparator<File>() {
        public int compare(File f1, File f2) {
          return f1.getName().compareTo(f2.getName());
        }
      });
      ListIterator<File> li = listFiles.listIterator();
      while (li.hasNext()) {
        File f = li.next();
        if (!isValidFile(f, date)) {
          li.remove();
        }
      }
      currFileIndex = 0;
    } catch (Exception e) {
      throw new ManifoldCFException("Unable create files list", e);
    }
  }

  public SolrCommandsInFile getNextCommand() throws InterruptedException {
    while (currFileIndex < listFiles.size()) {
      File file = listFiles.get(currFileIndex++);
      try{
        SolrCommandsInFile commandsInFile = crawlFile(file);
        return commandsInFile;
      } catch (Exception e){
        //stats.addXmlErrorCount();
        logger.error("Unable to parse document " + file.getAbsolutePath() + "please check this document");
      }
    }
    return null;
  }

  boolean isValidFile(File f, Date date) {
    if (
        (date == null || f.lastModified() > date.getTime())
            && this.getExtension(f).equals("xml")
        ) {
      return true;
    }
    return false;
  }

  public SolrCommandsInFile crawlFile(File documentPath) throws CurlyIndexerException {
    logger.info("Start analyzing file: " + documentPath.getAbsolutePath());
    SolrCommandsContainer c = solrCommandReader.parse(documentPath);
    return new SolrCommandsInFile(documentPath.getAbsolutePath(), c);
  }

  public SolrCommandsInFile crawlFile(String path, String document) throws CurlyIndexerException {
    logger.info("Start analyzing file string: ");
    logger.info("Parsing document string");
    SolrCommandsContainer c = solrCommandReader.parse(document);
    return new SolrCommandsInFile(path, c);
  }

  private String getExtension(File f) {
    String name = f.getName();
    int pos = name.lastIndexOf('.');
    String ext = name.substring(pos + 1);
    return ext;
  }

}
