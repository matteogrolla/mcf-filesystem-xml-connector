package org.apache.manifoldcf.crawler.connectors.filesystemxml.domain;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

    public enum Action {
      @SerializedName("include")
      INCLUDE,
      @SerializedName("exclude")
      EXCLUDE};
    public enum FilterObject {
      @SerializedName("file")
      FILE,
      @SerializedName("directory")
      DIRECTORY};

  }

  public static String serializeFileSelectorCriterias(List<FileSelectorCriteria> criterias){
    Gson gson = new Gson();
    String json = gson.toJson(criterias);
    return json;
  }

  public static List<FileSelectorCriteria> deserializeFileSelectorCriterias(String json){
    Gson gson = new Gson();
    Type criteriasType = new TypeToken<List<FileSelectorCriteria>>(){}.getType();
    List<FileSelectorCriteria> criterias = gson.fromJson(json, criteriasType);
    return criterias;
  }

}
