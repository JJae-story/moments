package com.uijae.moments.common.logging;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void controllerAdvice() {
  }

  @Before("controllerAdvice()")
  public void requestLogging(JoinPoint joinPoint) {
    MDC.put("traceId", UUID.randomUUID().toString());

    String className = joinPoint.getTarget().getClass().getName();
    String methodName = joinPoint.getSignature().getName();

    Object[] args = joinPoint.getArgs();

    log.info("REQUEST TRACING_ID -> {} | Class -> {} | Method -> {} | Args -> {}",
        MDC.get("traceId"), className, methodName, args);
  }

  @AfterReturning(pointcut = "controllerAdvice()", returning = "returnValue")
  public void responseLogging(JoinPoint joinPoint, Object returnValue) {
    String className = joinPoint.getTarget().getClass().getName();
    String methodName = joinPoint.getSignature().getName();

    log.info("RESPONSE TRACING_ID -> {} | Class -> {} | Method -> {} | Result -> {}",
        MDC.get("traceId"), className, methodName, returnValue);

    MDC.clear();
  }
}
