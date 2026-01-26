package com.gigtasker.common.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     * Assumes your package structure starts with 'com.gigtasker'
     */
    @Pointcut("within(com.gigtasker..*) && @annotation(org.springframework.web.bind.annotation.RestController)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Pointcut("within(com.gigtasker..*) && @annotation(org.springframework.stereotype.Service)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Advice that logs methods throwing exceptions.
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() || springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("❌ Exception in {}.{}() with cause = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL",
                e // Passing the exception here prints the stack trace
        );
    }

    /**
     * Advice that logs when a method is entered and exited.
     */
    @Around("applicationPackagePointcut() || springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("➡️ Enter: {}.{}() with argument[s] = {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        }
        try {
            // Measure execution time
            long start = System.currentTimeMillis();

            // Run the actual method
            Object result = joinPoint.proceed();

            long end = System.currentTimeMillis();

            if (log.isDebugEnabled()) {
                log.debug("⬅️ Exit: {}.{}() with result = {} (Time: {} ms)",
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        result,
                        (end - start));
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("⚠️ Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }
}
