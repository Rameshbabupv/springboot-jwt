package com.systech.nexus.common.aspect;

import com.systech.nexus.common.annotation.Loggable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(com.systech.nexus.common.annotation.Loggable) || " +
            "@within(com.systech.nexus.common.annotation.Loggable)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String correlationId = UUID.randomUUID().toString().substring(0, 8);

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();

        // Get @Loggable annotation (either on method or class)
        Loggable loggableAnnotation = method.getAnnotation(Loggable.class);
        if (loggableAnnotation == null) {
            loggableAnnotation = method.getDeclaringClass().getAnnotation(Loggable.class);
        }

        // Set correlation ID in MDC for request tracing
        MDC.put("correlationId", correlationId);

        try {
            logMethodEntry(className, methodName, joinPoint.getArgs(), loggableAnnotation, correlationId);

            Object result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            logMethodExit(className, methodName, result, executionTime, loggableAnnotation, correlationId);

            return result;

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            logMethodException(className, methodName, e, executionTime, correlationId);
            throw e;

        } finally {
            MDC.remove("correlationId");
        }
    }

    private void logMethodEntry(String className, String methodName, Object[] args,
                               Loggable annotation, String correlationId) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("[").append(correlationId).append("] ");
        logMessage.append("→ ENTERING: ").append(className).append(".").append(methodName).append("()");

        if (!annotation.description().isEmpty()) {
            logMessage.append(" - ").append(annotation.description());
        }

        if (annotation.logParameters() && args != null && args.length > 0) {
            logMessage.append(" | Parameters: ").append(Arrays.toString(args));
        }

        logger.info(logMessage.toString());
    }

    private void logMethodExit(String className, String methodName, Object result,
                              long executionTime, Loggable annotation, String correlationId) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("[").append(correlationId).append("] ");
        logMessage.append("← EXITING: ").append(className).append(".").append(methodName).append("()");

        if (annotation.logExecutionTime()) {
            logMessage.append(" | Execution time: ").append(executionTime).append("ms");
        }

        if (annotation.logResult() && result != null) {
            String resultStr = result.toString();
            if (resultStr.length() > 200) {
                resultStr = resultStr.substring(0, 200) + "...";
            }
            logMessage.append(" | Result: ").append(resultStr);
        }

        logger.info(logMessage.toString());
    }

    private void logMethodException(String className, String methodName, Exception e,
                                   long executionTime, String correlationId) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("[").append(correlationId).append("] ");
        logMessage.append("✗ EXCEPTION in: ").append(className).append(".").append(methodName).append("()");
        logMessage.append(" | Execution time: ").append(executionTime).append("ms");
        logMessage.append(" | Exception: ").append(e.getClass().getSimpleName());
        logMessage.append(" | Message: ").append(e.getMessage());

        logger.error(logMessage.toString(), e);
    }
}