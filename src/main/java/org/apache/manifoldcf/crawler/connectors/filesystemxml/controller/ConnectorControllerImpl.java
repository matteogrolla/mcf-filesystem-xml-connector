package org.apache.manifoldcf.crawler.connectors.filesystemxml.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.manifoldcf.core.interfaces.*;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.DocumentSpecificationWrapper;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.FileSelectorCriteria;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.ui.Messages;
import org.apache.manifoldcf.crawler.interfaces.DocumentSpecification;
import org.apache.manifoldcf.crawler.system.Logging;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 17/06/14
 * Time: 16:36
 * To change this template use File | Settings | File Templates.
 */
public class ConnectorControllerImpl implements ConnectorController {

  @Override
  public void outputConfigurationHeader(IThreadContext threadContext, IHTTPOutput out, ConfigParams parameters, List<String> tabsArray)
      throws ManifoldCFException, IOException {
    out.print("TO REMOVE: outputConfigurationHeader");
    /*out.print(
        "<script type=\"text/javascript\">\n"+
            "<!--\n"+
            "function checkConfigForSave()\n"+
            "{\n"+
            "  return true;\n"+
            "}\n"+
            "\n"+
            "//-->\n"+
            "</script>\n"
    );*/
  }

  @Override
  public void outputConfigurationBody(IThreadContext threadContext, IHTTPOutput out, ConfigParams parameters, String tabName)
      throws ManifoldCFException, IOException {
    out.print("TO REMOVE: outputConfigurationBody");
  }

  @Override
  public String processConfigurationPost(IThreadContext threadContext, IPostParameters variableContext, ConfigParams parameters)
      throws ManifoldCFException {
    return null;
  }

  @Override
  public void viewConfiguration(IThreadContext threadContext, IHTTPOutput out, Locale locale, ConfigParams parameters)
      throws ManifoldCFException, IOException {
    //default informations are fine
    //out.print("TO REMOVE: viewConfiguration");
    Map<String, String> paramMap = new HashMap<String, String>();
    Messages.outputResourceWithVelocity(out, locale, "viewConfiguration.html", paramMap, true);
  }

  @Override
  public void outputSpecificationHeader(IHTTPOutput out, Locale locale, DocumentSpecification ds, List<String> tabsArray)
      throws ManifoldCFException, IOException {
    out.print("TO REMOVE: outputSpecificationHeader");
    tabsArray.add(Messages.getString(locale, "FileConnector.Paths"));

    /*out.print(
        "<script type=\"text/javascript\">\n"+
            "<!--\n"+
            "function checkSpecification()\n"+
            "{\n"+
            "  // Does nothing right now.\n"+
            "  return true;\n"+
            "}\n"+
            "\n"+
            "function SpecOp(n, opValue, anchorvalue)\n"+
            "{\n"+
            "  eval(\"editjob.\"+n+\".value = \\\"\"+opValue+\"\\\"\");\n"+
            "  postFormSetAnchor(anchorvalue);\n"+
            "}\n"+
            "//-->\n"+
            "</script>\n"
    );*/
  }

  @Override
  public void outputSpecificationBody(IHTTPOutput out, Locale locale, DocumentSpecification ds, String tabName)
      throws ManifoldCFException, IOException {
    out.print("TO REMOVE: outputSpecificationBody");
    DocumentSpecificationWrapper dsw = new DocumentSpecificationWrapper(ds);
    String fileSelectorValue = dsw.getFileSelectionValue();

    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put("fileSelectorValue", fileSelectorValue);
    paramMap.put("TabName", tabName);
    Messages.outputResourceWithVelocity(out, locale, "outputSpecificationBody.vtl", paramMap, true);
    return;


  }

  @Override
  public String processSpecificationPost(IPostParameters variableContext, Locale locale, DocumentSpecification ds)
      throws ManifoldCFException {
    String fileSelectorValue = variableContext.getParameter("fileSelector_value");
    Logging.connectors.info("processSpecificationPost: " + fileSelectorValue);
    DocumentSpecificationWrapper dsw = new DocumentSpecificationWrapper(ds);
    dsw.setFileSelectionValue(fileSelectorValue);

    return null;
  }

  public void viewSpecification(IHTTPOutput out, Locale locale, DocumentSpecification ds)
      throws ManifoldCFException, IOException {
    out.print("TO REMOVE: viewSpecification");
    DocumentSpecificationWrapper dsw = new DocumentSpecificationWrapper(ds);
    String fileSelectorValue = dsw.getFileSelectionValue();

    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put("TabName", Messages.getBodyString(locale, "FileConnector.Paths"));
    paramMap.put("fileSelectorValue", fileSelectorValue);
    Messages.outputResourceWithVelocity(out, locale, "viewSpecificationBody.vtl", paramMap, true);
  }

}
