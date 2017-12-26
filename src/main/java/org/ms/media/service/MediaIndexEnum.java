package org.ms.media.service;

public enum MediaIndexEnum {
  TITLE("title"),
  ALBUM("album"),
  ARTIST("artist"),
  FILENAME("filename"),
  PATH("path");
  
  private String name;
  
  private MediaIndexEnum(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
