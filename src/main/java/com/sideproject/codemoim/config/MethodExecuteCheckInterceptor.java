package com.sideproject.codemoim.config;//package com.sideproject.codemoim.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.aopalliance.intercept.MethodInterceptor;
//import org.aopalliance.intercept.MethodInvocation;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class MethodExecuteCheckInterceptor implements MethodInterceptor {
//    @Override
//    public Object invoke(MethodInvocation invocation) throws Throwable {
//        log.info("================= Before Execute Method =================");
//
//        Object returnValue = invocation.proceed();
//
//        log.info("================= After Execute Method =================");
//
//        return returnValue;
//    }
//}