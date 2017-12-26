package org.ms.media.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.ms.media.util.Mp3Util;
import org.ms.media.vo.Mp3FileVo;
import org.ms.media.vo.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Mayank
 *
 */
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MediaIndexer {
  private static final Logger LOGGER = LoggerFactory.getLogger(MediaIndexer.class);
  
  @Autowired
  private Mp3Util mp3Util;
  
  private String sourceDir = "E:\\";
  private String indexPath = "C:\\temp\\luceneindex";
  
  public ScanResult scan() throws IOException {
    Date start = new Date();
    
    Directory dir = FSDirectory.open(Paths.get(indexPath));
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
    // Create a new index in the directory, removing any
    // previously indexed documents:
    iwc.setOpenMode(OpenMode.CREATE_OR_APPEND); //iwc.setOpenMode(OpenMode.CREATE);
    
    // Optional: for better indexing performance, if you
    // are indexing many documents, increase the RAM
    // buffer.  But if you do this, increase the max heap
    // size to the JVM (eg add -Xmx512m or -Xmx1g):
    //
    // iwc.setRAMBufferSizeMB(256.0);
    IndexWriter writer = new IndexWriter(dir, iwc);
    writer.deleteAll(); //clear existing records
    
    // call indexDocs
    
    ScanResult result = mp3Util.scanMp3Files(Paths.get(sourceDir), true);
    indexDocs(writer, result.getData());
    writer.close();
    
    Date end = new Date();
    
    LOGGER.info("Indexing complete in {} milliseconds.", end.getTime() - start.getTime());
    System.out.println(end.getTime() - start.getTime() + " total milliseconds");
    result.setElapsedTime(end.getTime() - start.getTime());
    
    return result;
  }
  
  /**
   * Write MP3 Information to Lucene FS.
   * 
   * @param writer
   * @param mp3Files
   * @throws IOException
   */
  private void indexDocs(IndexWriter writer, List<Mp3FileVo> Mp3FileVos) throws IOException {
    for(Mp3FileVo mp3File: Mp3FileVos) {
      Document doc = new Document();
      
      doc.add(new TextField("title", mp3File.getTitle(), Field.Store.YES));
      doc.add(new TextField("album", mp3File.getAlbum(), Field.Store.YES));
      doc.add(new TextField("artist", mp3File.getArtist(), Field.Store.YES));
      doc.add(new TextField("filename", mp3File.getFileName(), Field.Store.YES));
      doc.add(new TextField("path", mp3File.getFilePath(), Field.Store.YES));

      writer.addDocument(doc);
    }
  }
  
  /**
   * Search title in index.
   * 
   * @param strQuery
   * @throws IOException
   * @throws ParseException
   */
  public List<Mp3FileVo> search(String strQuery) throws IOException, ParseException {
    final List<Mp3FileVo> searchResult = new LinkedList<>();
    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
    IndexSearcher searcher = new IndexSearcher(reader);
    //Analyzer analyzer = new StandardAnalyzer();
    
    Map<String, Float> boosts = new HashMap<String, Float>(2);
    boosts.put(MediaIndexEnum.TITLE.getName(), new Float(4) );
    boosts.put(MediaIndexEnum.ALBUM.getName(), new Float(3) );
    boosts.put(MediaIndexEnum.ARTIST.getName(), new Float(3) );
    boosts.put(MediaIndexEnum.FILENAME.getName(), new Float(1) );
    
    MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title", "album", "artist", "filename"}, new StandardAnalyzer(), boosts);
    Query query = parser.parse(strQuery);
    
    LOGGER.info("Searching for: {}", query.toString("title"));
    
    TopDocs results = searcher.search(query, 50);
    ScoreDoc[] hits = results.scoreDocs;
    
    LOGGER.info("Hits: {}", hits.length);
    
    for (int i = 0; i < hits.length; i++) {
      Document document = searcher.doc(hits[i].doc);
      final Mp3FileVo vo = new Mp3FileVo();
      vo.setDocid(hits[i].doc);
      vo.setAlbum(document.get(MediaIndexEnum.ALBUM.getName()));
      vo.setArtist(document.get(MediaIndexEnum.ARTIST.getName()));
      vo.setFileName(document.get(MediaIndexEnum.FILENAME.getName()));
      vo.setTitle(document.get(MediaIndexEnum.TITLE.getName()));

      searchResult.add(vo);
    }
    
    reader.close();
    return searchResult;
  }
  
  /**
   * Get path form Lucene index based on docid
   * 
   * @param id
   * @return Path
   * @throws IOException 
   */
  public Path getFilePath(Integer id) throws IOException {
    LOGGER.info("Requested doc: {}", id);
    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
    Document doc = reader.document(id);
    LOGGER.info("Doc: {}", doc);
    LOGGER.info("File path: {}", doc.get(MediaIndexEnum.PATH.name()));
    reader.close();
    Path p = Paths.get(doc.get(MediaIndexEnum.PATH.getName()));
    return p;
  }
}
