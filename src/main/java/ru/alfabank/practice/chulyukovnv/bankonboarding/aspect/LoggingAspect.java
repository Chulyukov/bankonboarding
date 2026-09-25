package ru.alfabank.practice.chulyukovnv.bankonboarding.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Around("@annotation(ru.alfabank.practice.chulyukovnv.bankonboarding.aspect.Log)")
    public Object aroundLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getDeclaringTypeName();
        Object[] agrs = joinPoint.getArgs();
        log.info("Request {} => {}", methodName, Arrays.toString(agrs));
        Object result;
        try {
            result = joinPoint.proceed();

        } catch (Throwable e) {
            log.warn("Error: {}", e.getMessage());
            throw e;
        }
        log.info("Response {} <= {}", methodName, result);
        return result;
    }

}
