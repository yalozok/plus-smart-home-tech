package ru.yandex.practicum.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(ru.yandex.practicum.logging.Loggable)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("==>: {}", joinPoint.getSignature());
        Object[] args = joinPoint.getArgs();
        logger.info("Request Parameters: {}", args);

        Object result = joinPoint.proceed();

        logger.info("<==: {} - Response: {}", joinPoint.getSignature(), result);
        return result;
    }

}
