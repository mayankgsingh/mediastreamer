package org.ms.media.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.ms.media.service.MediaIndexer;
import org.ms.media.vo.Mp3FileVo;
import org.ms.media.vo.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping(path="/media")
public class MediaController {
  private static final Logger LOGGER = LoggerFactory.getLogger(MediaController.class);
  
  @Autowired
  private MediaIndexer mediaIndexer;
  
  /**
   * Initiate scan of file(s).
   * 
   * @return Map<String, Object>
   * @throws IOException
   */
  @RequestMapping(path="/scan", produces = "application/json", method=RequestMethod.GET)
  public ResponseEntity<Map<String, Integer>> scanFiles() throws IOException {
    LOGGER.info("Scanning request...");
    ScanResult result = mediaIndexer.scan();
    //ScanResult result = new ScanResult();
    result.setData(result.getData());
    result.setSkippedFiles(result.getSkippedFiles());
    
    Map<String, Integer> data = new HashMap<>();
    data.put("indexedFiles", result.getData().size());
    data.put("skippedFiles", result.getSkippedFiles().size());
    ResponseEntity<Map<String, Integer>> response = new ResponseEntity<Map<String,Integer>>(data, HttpStatus.OK);
    
    return response;
  }
  
  /**
   * Search Title REST end point
   * 
   * @param txtSearch
   * @return ResponseEntity<Map<String, List<Mp3FileVo>>>
   * @throws IOException
   * @throws ParseException 
   */
  @RequestMapping(path="/search/{txtSearch}", produces = "application/json", method=RequestMethod.GET)
  public ResponseEntity<Map<String, List<Mp3FileVo>>> search(@PathVariable(name="txtSearch") final String txtSearch) throws IOException, ParseException {
    LOGGER.info("Search for {}", txtSearch);
    List<Mp3FileVo> data = mediaIndexer.search(txtSearch);
    Map<String, List<Mp3FileVo>> result = new HashMap<>();
    result.put("data", data);
    
    ResponseEntity<Map<String, List<Mp3FileVo>>> response = new ResponseEntity<Map<String, List<Mp3FileVo>>>(result, HttpStatus.OK);
    return response;
  }
  
  /**
   * Returns media stream.
   * 
   * @param id
   * @return
   * @throws IOException
   */
  @RequestMapping("/play/{id}")
  public StreamingResponseBody playMedia(@PathVariable(name = "id") Integer id) throws IOException {
    Path f = mediaIndexer.getFilePath(id);
    InputStream in = Files.newInputStream(f, StandardOpenOption.READ);
    final HttpHeaders headers = new HttpHeaders();
    
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentLength(Files.size(f));
    //headers.add("filename", "media.mp3");
    return (os) -> {
      IOUtils.copy(in, os);
    };
  }
}
