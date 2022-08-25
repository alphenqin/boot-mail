package com.alphen.mall.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/*
* 打印请求和响应信息
* */
@Aspect
@Component
public class WebLogAspect {

    private final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    @Pointcut("execution(public * com.alphen.mall.controller.*.*(..))")
    public void webLog(){
    }


    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint){
//        收到请求的信息，并记录
//        获取请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("URL :"+request.getRequestURL().toString());
        logger.info("HTTP_METHOD :"+request.getMethod());
        logger.info("IP :"+request.getRemoteAddr());
        logger.info("ClASS_METHOD :"+joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName());
        logger.info("ARGS :"+ Arrays.toString(joinPoint.getArgs()));
    }

//    返回一个res对象
    @AfterReturning(returning = "res",pointcut = "webLog()")
    public void doAfterReturning(Object res) throws JsonProcessingException {
//        处理完请求返回内容
        logger.info("RESPONSE :"+new ObjectMapper().writeValueAsString(res));
    }
}
