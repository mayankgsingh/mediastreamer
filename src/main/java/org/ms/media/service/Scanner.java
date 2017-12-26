package org.ms.media.service;

import java.nio.file.Path;

import org.ms.media.vo.Mp3FileVo;

public interface Scanner {
  
  /**
   * Scans for files at given path.
   * 
   * @param path
   */
  public void scan(Path path);

  /**
   * It should retrieve all properties of files which needs to be stored in Lucene index.
   * @param path
   */
  public Mp3FileVo scanFile(Path path);
}
