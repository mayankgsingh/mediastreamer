package org.ms.media.vo;

public class Mp3FileVo {
  
  private Integer docid;
  private String fileName;
  private String filePath;
  private String title;
  private String album;
  private String artist;
  
  public Integer getDocid() {
    return docid;
  }

  public void setDocid(Integer docid) {
    this.docid = docid;
  }

  public String getAlbum() {
    return album;
  }
  
  public void setAlbum(String album) {
    this.album = album;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  @Override
  public String toString() {
    return "Mp3FileVo [docid=" + docid + ", fileName=" + fileName + ", filePath=" + filePath + ", title=" + title
        + ", album=" + album + ", artist=" + artist + "]";
  }
  
}
