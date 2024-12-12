package com.idle.kb_i_dle_backend.domain.member.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {

    @Around("execution(* com.idle.kb_i_dle_backend.domain.master.service.MasterService.updateStockPrices())")
    public Object measureUpdateStockPricesExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureExecutionTime(joinPoint, "updateStockPrices");
    }

    @Around("execution(* com.idle.kb_i_dle_backend.domain.master.service.MasterService.updateStockPricesBefore())")
    public Object measureUpdateStockPricesBeforeExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureExecutionTime(joinPoint, "updateStockPricesBefore");
    }

    private Object measureExecutionTime(ProceedingJoinPoint joinPoint, String methodName) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.error("{} executed in {} ms", methodName, executionTime);
        return proceed;
    }
}
