package org.ms.media.util;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.ms.media.vo.Mp3FileVo;
import org.ms.media.vo.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class Mp3Util {

  private static final Logger LOGGER = LoggerFactory.getLogger(Mp3Util.class);
  private static final String MP3_EXTN = ".mp3";

  public ScanResult scanMp3Files(final Path dir, boolean recursive) {
    final List<Mp3FileVo> data = new LinkedList<>();
    final List<Path> skipList = new LinkedList<>();

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
      for (Path file : stream) {
        if (Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS)) {
          if (recursive) {
            ScanResult sr = scanMp3Files(file, recursive);
            data.addAll(sr.getData());
            skipList.addAll(sr.getSkippedFiles());
          }
        } else {
          if (Files.isReadable(file)) {
            final String fileName = file.getFileName().toString();
            if (fileName.endsWith(MP3_EXTN)) {
              //result.add(file);
              try {
                data.add(readMp3TagInfo(file));
              } catch (Exception e) {
                skipList.add(file);
                LOGGER.warn("Skipping - {}", file);
              }
            }
          }
        }
      }
    } catch (IOException | DirectoryIteratorException ex) {
      // IOException can never be thrown by the iteration.
      // In this snippet, it can only be thrown by newDirectoryStream.
      LOGGER.error(ex.getMessage(), ex);
    }

    if(!skipList.isEmpty()) {
      System.out.println("Skip List:");

      for(Path p: skipList)
        System.out.println(p);
    }
     
    final ScanResult result = new ScanResult();
    result.setData(data);
    result.setSkippedFiles(skipList);
    
    return result;
  }

  private Mp3FileVo readMp3TagInfo(Path path) throws Exception {
    try {
      MP3File f = (MP3File) AudioFileIO.read(path.toFile());
      Tag tag = f.getTag();
      
      final Mp3FileVo vo = new Mp3FileVo();
      vo.setFilePath(path.toString());
      vo.setAlbum(tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM));
      vo.setArtist(tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST));
      vo.setFileName(path.getFileName().toString());
      vo.setTitle(tag.getFirst(ID3v24Frames.FRAME_ID_TITLE));
      
      return vo;
    } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
      LOGGER.error("For path: {}", path);
      LOGGER.error(e.getMessage(), e);
      throw e;
    }
  }
}
