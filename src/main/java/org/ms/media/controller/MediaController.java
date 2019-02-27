package org.ms.media.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;
import org.ms.media.service.MediaIndexer;
import org.ms.media.vo.Mp3FileVo;
import org.ms.media.vo.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping(path="/media")
@Scope(scopeName=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
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
  public StreamingResponseBody playMedia(@PathVariable(name = "id") final Integer id, @RequestHeader("Range") String range) throws IOException {
    Path f = mediaIndexer.getFilePath(id);
    InputStream in = Files.newInputStream(f, StandardOpenOption.READ);
    final HttpHeaders headers = new HttpHeaders();
    final int DEFAULT_SIZE = 1024 * 1024;
    
    LOGGER.info("Received Offset: {}", range);
    range = range.replace("bytes=", "");
    String[] reqOffset = range.split("-");
    int offset = Integer.parseInt(reqOffset[0]);
    int size;
    if(reqOffset.length != 2 || StringUtils.isEmpty(reqOffset[1])) {
      size = DEFAULT_SIZE;
    } else {
      size = Integer.parseInt(reqOffset[1]) - offset + 1;
    }
    
    LOGGER.info("Parsing Offset: {}", offset);
    
    byte[] buffer = new byte[size];
    
    
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentLength(Files.size(f));
    
    //headers.add("filename", "media.mp3");
    return (os) -> {
      try {
        int bytesAmount = 0;
        while ((bytesAmount = in.read(buffer, offset, size)) > 0) {
          os.write(buffer, 0, bytesAmount);
        }
      } catch(SocketTimeoutException e) {
        LOGGER.warn(e.getMessage());
      } finally {
        in.close();
        os.close();
      }
    };
  }
}
