package ru.alfabank.practice.chulyukovnv.bankonboarding.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Around("@annotation(ru.alfabank.practice.chulyukovnv.bankonboarding.aspect.Log)")
    public Object aroundLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        UUID uuid = UUID.randomUUID();
        String methodName = joinPoint.getSignature().getDeclaringTypeName();
        Object[] agrs = joinPoint.getArgs();
        log.info("{} — Request {} => {}", uuid, methodName, Arrays.toString(agrs));
        Object result = joinPoint.proceed();
        log.info("{} — Response {} <= {}", uuid, methodName, result);
        return result;
    }

}
