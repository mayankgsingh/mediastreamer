package org.ms.media.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@WebAppConfiguration
@EnableWebMvc
public class AppConfig {

  @Autowired
  private Environment env;

  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    configurer.setDefaultTimeout(-1);
    configurer.setTaskExecutor(asyncTaskExecutor());
  }
    
  @Bean
  public AsyncTaskExecutor asyncTaskExecutor() {
    return new SimpleAsyncTaskExecutor("async");
  }
}
