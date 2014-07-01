package org.apache.manifoldcf.crawler.connectors.filesystemxml.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.manifoldcf.crawler.connectors.filesystemxml.domain.FileSelectorCriteria;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 30/06/14
 * Time: 10:05
 * To change this template use File | Settings | File Templates.
 */
public class FileSelectorCriteriaGsonTest {

  @Test
  public void testSerialization(){
    FileSelectorCriteria criteria = new FileSelectorCriteria();
    criteria.path = "pathA";

    FileSelectorCriteria.Filter filter = new FileSelectorCriteria.Filter("include", "file", "*");
    criteria.filters.add(filter);

    List<FileSelectorCriteria> criterias = new ArrayList<FileSelectorCriteria>();
    criterias.add(criteria);

    String json = FileSelectorCriteria.serializeFileSelectorCriterias(criterias);

    assertEquals("[{\"path\":\"pathA\",\"filters\":[{\"action\":\"include\",\"object\":\"file\",\"match\":\"*\"}]}]", json);
  }

  @Test
  public void testDeserialization(){
    String json = "[{\"path\":\"pathA\",\"filters\":[{\"action\":\"include\",\"object\":\"file\",\"match\":\"*\"}]}]";

    List<FileSelectorCriteria> criterias = FileSelectorCriteria.deserializeFileSelectorCriterias(json);

    FileSelectorCriteria criteria = criterias.get(0);
    assertEquals("pathA", criteria.path);
    FileSelectorCriteria.Filter filter = criteria.filters.get(0);
    assertEquals("*", filter.match);
    assertEquals(FileSelectorCriteria.Filter.Action.INCLUDE, filter.action);
    assertEquals(FileSelectorCriteria.Filter.FilterObject.FILE, filter.object);
  }

}
