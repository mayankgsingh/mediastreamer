package org.ms.media.vo;

import java.nio.file.Path;
import java.util.List;

public class ScanResult {
  
  private List<Mp3FileVo> data;
  private List<Path> skippedFiles;
  private Long elapsedTime;  //in milliseconds
  private String sourceDir;
  
  public List<Mp3FileVo> getData() {
    return data;
  }
  
  public void setData(List<Mp3FileVo> data) {
    this.data = data;
  }

  public List<Path> getSkippedFiles() {
    return skippedFiles;
  }

  public void setSkippedFiles(List<Path> skippedFiles) {
    this.skippedFiles = skippedFiles;
  }

  public Long getElapsedTime() {
    return elapsedTime;
  }

  public void setElapsedTime(Long elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  public String getSourceDir() {
    return sourceDir;
  }

  public void setSourceDir(String sourceDir) {
    this.sourceDir = sourceDir;
  }
}
