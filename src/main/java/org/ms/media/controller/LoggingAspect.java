package org.ms.media.controller;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect class added to track/log REST end point usage.
 * 
 * @author Mayank
 *
 */
@Aspect
@Component
public class LoggingAspect {
  
  public static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);
  
  @Before("execution(* org.ms.media.controller.MediaController.*(..))")
  public void logBefore(JoinPoint joinPoint) {
    LOG.info("Executing: {} / {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
  }
  
}
