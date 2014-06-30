package org.apache.manifoldcf.crawler.connectors.filesystemxml.persistence.solrcommandio;

public class DummySolrField {

  private String name;
  private String testo;
  private float boost = 1.0f;

  public void setName(String name) {
    this.name = name;
  }

  public void setBoost(float boost) {
    this.boost = boost;
  }

  public float getBoost() {
    return boost;
  }

  public String getName() {
    return name;
  }

  public void setTesto(String testo) {
    this.testo = testo;
  }

  public String getTesto() {
    return testo;
  }

}
