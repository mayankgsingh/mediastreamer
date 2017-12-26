package org.ms.media.util;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
  private static String MP3_EXTN = ".mp3";
  
  public List<Path> scanMp3Files(final Path dir, boolean recursive) {
    final List<Path> result = new LinkedList<>();
    
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
      for (Path file : stream) {
        if(Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS)) {
          if(recursive) {
            result.addAll(scanMp3Files(file, recursive));
          }
        } else {
          if(Files.isReadable(file)) {
            final String fileName = file.getFileName().toString();
            if(fileName.endsWith(MP3_EXTN)) {
              result.add(file);
            }
          }
        }
      }
    } catch (IOException | DirectoryIteratorException ex) {
      // IOException can never be thrown by the iteration.
      // In this snippet, it can only be thrown by newDirectoryStream.
      LOGGER.error(ex.getMessage(), ex);
    }
    
    return result;
  }
}
