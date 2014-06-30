package org.apache.manifoldcf.crawler.connectors.filesystemxml.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 30/06/14
 * Time: 09:44
 * To change this template use File | Settings | File Templates.
 */
public class FileSelectorCriteria {
  public String path;
  public List<Filter> filters = new ArrayList<Filter>();

  public static class Filter {
    public Action action;
    public FilterObject object;
    public String match;

    public Filter(String action, String object, String match){
      this.action = Action.valueOf(action.toUpperCase());
      this.object = FilterObject.valueOf(object.toUpperCase());
      this.match = match;
    }

    public enum Action {INCLUDE, EXCLUDE};
    public enum FilterObject {FILE, DIRECTORY};

  }
}
