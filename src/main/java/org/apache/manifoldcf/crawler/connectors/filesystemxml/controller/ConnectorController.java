package org.apache.manifoldcf.crawler.connectors.filesystemxml.controller;

import org.apache.manifoldcf.core.interfaces.*;
import org.apache.manifoldcf.crawler.interfaces.DocumentSpecification;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

// UI support methods.
//
// These support methods come in two varieties.  The first bunch is involved in setting up connection configuration information.  The second bunch
// is involved in presenting and editing document specification information for a job.  The two kinds of methods are accordingly treated differently,
// in that the first bunch cannot assume that the current connector object is connected, while the second bunch can.  That is why the first bunch
// receives a thread context argument for all UI methods, while the second bunch does not need one (since it has already been applied via the connect()
// method, above).
public interface ConnectorController {


  /** Output the configuration header section.
   * This method is called in the head section of the connector's configuration page.  Its purpose is to add the required tabs to the list, and to output any
   * javascript methods that might be needed by the configuration editing HTML.
   *@param threadContext is the local thread context.
   *@param out is the output to which any HTML should be sent.
   *@param parameters are the configuration parameters, as they currently exist, for this connection being configured.
   *@param tabsArray is an array of tab names.  Add to this array any tab names that are specific to the connector.
   */
  public void outputConfigurationHeader(IThreadContext threadContext, IHTTPOutput out, ConfigParams parameters, List<String> tabsArray)
      throws ManifoldCFException, IOException;

  /** Output the configuration body section.
   * This method is called in the body section of the connector's configuration page.  Its purpose is to present the required form elements for editing.
   * The coder can presume that the HTML that is output from this configuration will be within appropriate <html>, <body>, and <form> tags.  The name of the
   * form is "editconnection".
   *@param threadContext is the local thread context.
   *@param out is the output to which any HTML should be sent.
   *@param parameters are the configuration parameters, as they currently exist, for this connection being configured.
   *@param tabName is the current tab name.
   */
  public void outputConfigurationBody(IThreadContext threadContext, IHTTPOutput out, ConfigParams parameters, String tabName)
      throws ManifoldCFException, IOException;

  /** Process a configuration post.
   * This method is called at the start of the connector's configuration page, whenever there is a possibility that form data for a connection has been
   * posted.  Its purpose is to gather form information and modify the configuration parameters accordingly.
   * The name of the posted form is "editconnection".
   *@param threadContext is the local thread context.
   *@param variableContext is the set of variables available from the post, including binary file post information.
   *@param parameters are the configuration parameters, as they currently exist, for this connection being configured.
   *@return null if all is well, or a string error message if there is an error that should prevent saving of the connection (and cause a redirection to an error page).
   */
  public String processConfigurationPost(IThreadContext threadContext, IPostParameters variableContext, ConfigParams parameters)
      throws ManifoldCFException;

  /** View configuration.
   * This method is called in the body section of the connector's view configuration page.  Its purpose is to present the connection information to the user.
   * The coder can presume that the HTML that is output from this configuration will be within appropriate <html> and <body> tags.
   *@param threadContext is the local thread context.
   *@param out is the output to which any HTML should be sent.
   *@param parameters are the configuration parameters, as they currently exist, for this connection being configured.
   */
  public void viewConfiguration(IThreadContext threadContext, IHTTPOutput out, Locale locale, ConfigParams parameters)
      throws ManifoldCFException, IOException;

  /** Output the specification header section.
   * This method is called in the head section of a job page which has selected a repository connection of the current type.  Its purpose is to add the required tabs
   * to the list, and to output any javascript methods that might be needed by the job editing HTML.
   *@param out is the output to which any HTML should be sent.
   *@param ds is the current document specification for this job.
   *@param tabsArray is an array of tab names.  Add to this array any tab names that are specific to the connector.
   */
  public void outputSpecificationHeader(IHTTPOutput out, Locale locale, DocumentSpecification ds, List<String> tabsArray)
      throws ManifoldCFException, IOException;

  /** Output the specification body section.
   * This method is called in the body section of a job page which has selected a repository connection of the current type.  Its purpose is to present the required form elements for editing.
   * The coder can presume that the HTML that is output from this configuration will be within appropriate <html>, <body>, and <form> tags.  The name of the
   * form is "editjob".
   *@param out is the output to which any HTML should be sent.
   *@param ds is the current document specification for this job.
   *@param tabName is the current tab name.
   */
  public void outputSpecificationBody(IHTTPOutput out, Locale locale, DocumentSpecification ds, String tabName)
      throws ManifoldCFException, IOException;

  /** Process a specification post.
   * This method is called at the start of job's edit or view page, whenever there is a possibility that form data for a connection has been
   * posted.  Its purpose is to gather form information and modify the document specification accordingly.
   * The name of the posted form is "editjob".
   *@param variableContext contains the post data, including binary file-upload information.
   *@param ds is the current document specification for this job.
   *@return null if all is well, or a string error message if there is an error that should prevent saving of the job (and cause a redirection to an error page).
   */
  public String processSpecificationPost(IPostParameters variableContext, Locale locale, DocumentSpecification ds)
      throws ManifoldCFException;

  /** View specification.
   * This method is called in the body section of a job's view page.  Its purpose is to present the document specification information to the user.
   * The coder can presume that the HTML that is output from this configuration will be within appropriate <html> and <body> tags.
   *@param out is the output to which any HTML should be sent.
   *@param ds is the current document specification for this job.
   */
  public void viewSpecification(IHTTPOutput out, Locale locale, DocumentSpecification ds)
      throws ManifoldCFException, IOException;
}
