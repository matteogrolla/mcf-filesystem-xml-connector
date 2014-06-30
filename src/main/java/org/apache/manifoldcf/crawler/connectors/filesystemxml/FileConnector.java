/* $Id: FileConnector.java 995085 2010-09-08 15:13:38Z kwright $ */

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.manifoldcf.crawler.connectors.filesystemxml;

import org.apache.manifoldcf.agents.interfaces.ServiceInterruption;
import org.apache.manifoldcf.core.interfaces.*;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.persistence.solrcommandio.SolrCommandFactoryImpl;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.persistence.solrcommandio.SolrCommandReader;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.service.core.CrawlingService;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.service.core.CrawlingServiceImpl;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.service.core.DocumentsFilterImpl;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.controller.ConnectorController;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.controller.ConnectorControllerImpl;
import org.apache.manifoldcf.crawler.interfaces.DocumentSpecification;
import org.apache.manifoldcf.crawler.interfaces.IDocumentIdentifierStream;
import org.apache.manifoldcf.crawler.interfaces.IProcessActivity;
import org.apache.manifoldcf.crawler.interfaces.IVersionActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * This is the "repository connector" for a file system.  It's a relative of the share crawler, and should have
 * comparable basic functionality, with the exception of the ability to use ActiveDirectory and look at other shares.
 */
public class FileConnector extends org.apache.manifoldcf.crawler.connectors.BaseRepositoryConnector {
  public static final String _rcsid = "@(#)$Id: FileConnector.java 995085 2010-09-08 15:13:38Z kwright $";

  // Activities that we know about
  public final static String ACTIVITY_READ = "read document";

  // Relationships we know about
  public static final String RELATIONSHIP_CHILD = "child";

  // Activities list
  public static final String[] activitiesList = new String[]{ACTIVITY_READ};

  CrawlingService crawlingService;
  ConnectorController connectorController;

  // Parameters that this connector cares about
  // public final static String ROOTDIRECTORY = "rootdirectory";

  // Local data
  // protected File rootDirectory = null;

  /**
   * Constructor.
   */
  public FileConnector() {
    injectDependencies();
  }

  private void injectDependencies() {
    CrawlingServiceImpl crawlingService = new CrawlingServiceImpl();

    SolrCommandReader commandReader;
    commandReader = new SolrCommandReader();
    commandReader.setSolrCommandFactory(new SolrCommandFactoryImpl());
    crawlingService.setCommandReader(commandReader);

    DocumentsFilterImpl documentsFilter = new DocumentsFilterImpl();
    crawlingService.setDocumentsFilter(documentsFilter);

    this.crawlingService = crawlingService;


    connectorController = new ConnectorControllerImpl();
  }

  /**
   * Tell the world what model this connector uses for getDocumentIdentifiers().
   * This must return a model value as specified above.
   *
   * @return the model type value.
   */
  @Override
  public int getConnectorModel() {
    return MODEL_CHAINED_ADD_CHANGE;
  }

  /**
   * Return the list of relationship types that this connector recognizes.
   *
   * @return the list.
   */
  @Override
  public String[] getRelationshipTypes() {
    return new String[]{RELATIONSHIP_CHILD};
  }

  /**
   * List the activities we might report on.
   */
  @Override
  public String[] getActivitiesList() {
    return activitiesList;
  }

  /**
   * For any given document, list the bins that it is a member of.
   */
  @Override
  public String[] getBinNames(String documentIdentifier) {
    return new String[]{""};
  }

  @Override
  public IDocumentIdentifierStream getDocumentIdentifiers(DocumentSpecification spec, long startTime, long endTime)
      throws ManifoldCFException {
    if (crawlingService == null) {
      injectDependencies();
    }
    return crawlingService.getDocumentIdentifiers(spec, startTime, endTime);
  }

  @Override
  public String[] getDocumentVersions(String[] documentIdentifiers, String[] oldVersions, IVersionActivity activities,
                                      DocumentSpecification spec, int jobMode, boolean usesDefaultAuthority)
      throws ManifoldCFException, ServiceInterruption {
    return crawlingService.getDocumentVersions(documentIdentifiers, oldVersions, activities, spec, jobMode, usesDefaultAuthority);
  }


  @Override
  public void processDocuments(String[] documentIdentifiers, String[] versions, IProcessActivity activities, DocumentSpecification spec, boolean[] scanOnly)
      throws ManifoldCFException, ServiceInterruption {
    crawlingService.processDocuments(documentIdentifiers, versions, activities, spec, scanOnly);
  }


  @Override
  public void outputConfigurationHeader(IThreadContext threadContext, IHTTPOutput out, ConfigParams parameters, List<String> tabsArray)
      throws ManifoldCFException, IOException {
    connectorController.outputConfigurationHeader(threadContext, out, parameters, tabsArray);
  }

  public void outputConfigurationBody(IThreadContext threadContext, IHTTPOutput out, ConfigParams parameters, String tabName)
      throws ManifoldCFException, IOException {
  }

  @Override
  public String processConfigurationPost(IThreadContext threadContext, IPostParameters variableContext, ConfigParams parameters)
      throws ManifoldCFException {
    return connectorController.processConfigurationPost(threadContext, variableContext, parameters);
  }

  @Override
  public void viewConfiguration(IThreadContext threadContext, IHTTPOutput out, Locale locale, ConfigParams parameters)
      throws ManifoldCFException, IOException {
    connectorController.viewConfiguration(threadContext, out, locale, parameters);
  }

  /**
   * Output the specification body section.
   * This method is called in the body section of a job page which has selected a repository connection of the current type.  Its purpose is to present the required form elements for editing.
   * The coder can presume that the HTML that is output from this configuration will be within appropriate <html>, <body>, and <form> tags.  The name of the
   * form is "editjob".
   *
   * @param out     is the output to which any HTML should be sent.
   * @param ds      is the current document specification for this job.
   * @param tabName is the current tab name.
   */
  @Override
  public void outputSpecificationBody(IHTTPOutput out, Locale locale, DocumentSpecification ds, String tabName)
      throws ManifoldCFException, IOException {
    connectorController.outputSpecificationBody(out, locale, ds, tabName);
  }

  @Override
  public void outputSpecificationHeader(IHTTPOutput out, Locale locale, DocumentSpecification ds, List<String> tabsArray)
      throws ManifoldCFException, IOException {
    connectorController.outputSpecificationHeader(out, locale, ds, tabsArray);
  }

  /**
   * Process a specification post.
   * This method is called at the start of job's edit or view page, whenever there is a possibility that form data for a connection has been
   * posted.  Its purpose is to gather form information and modify the document specification accordingly.
   * The name of the posted form is "editjob".
   *
   * @param variableContext contains the post data, including binary file-upload information.
   * @param ds              is the current document specification for this job.
   * @return null if all is well, or a string error message if there is an error that should prevent saving of the job (and cause a redirection to an error page).
   */
  @Override
  public String processSpecificationPost(IPostParameters variableContext, Locale locale, DocumentSpecification ds)
      throws ManifoldCFException {
    return connectorController.processSpecificationPost(variableContext, locale, ds);
  }

  /**
   * View specification.
   * This method is called in the body section of a job's view page.  Its purpose is to present the document specification information to the user.
   * The coder can presume that the HTML that is output from this configuration will be within appropriate <html> and <body> tags.
   *
   * @param out is the output to which any HTML should be sent.
   * @param ds  is the current document specification for this job.
   */
  @Override
  public void viewSpecification(IHTTPOutput out, Locale locale, DocumentSpecification ds)
      throws ManifoldCFException, IOException {
    connectorController.viewSpecification(out, locale, ds);

  }

}
