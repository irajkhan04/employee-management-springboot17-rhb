package com.example.assignment.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class RequestResponseLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingAspect.class);

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logControllerRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        long startedAt = System.currentTimeMillis();

        // For a demo, log controller arguments. In production, redact sensitive fields
        // and avoid logging credentials, tokens, payment details, or personal data.
        log.info("Incoming controller request: method={}, args={}",
                method, Arrays.deepToString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            log.info("Controller response: method={}, durationMs={}, result={}",
                    method, System.currentTimeMillis() - startedAt, result);
            return result;
        } catch (Throwable ex) {
            log.error("Controller error: method={}, durationMs={}, exceptionType={}",
                    method, System.currentTimeMillis() - startedAt,
                    ex.getClass().getSimpleName());
            throw ex;
        }
    }
}
