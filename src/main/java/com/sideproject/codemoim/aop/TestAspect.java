package com.sideproject.codemoim.aop;//package com.sideproject.codemoim.aop;
//
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//
//@Slf4j
//@Aspect
//@Component
//public class TestAspect {
//
//    @Pointcut("execution(* com.sideproject.codemoim.service.BoardService.*(..))")
//    public void getCallMethod(){}
//
//    @Around("getCallMethod()")
//    public Object outputCrudServiceLogging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        Object result = null;
//
//        log.info("================= Before Execute Method =================");
//
//        result = proceedingJoinPoint.proceed();
//
//        log.info("================= After Execute Method =================");
//
//        return result;
//    }
//
//}
