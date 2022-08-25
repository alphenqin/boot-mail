package com.alphen.mall.exception;

import com.alphen.mall.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/*
* 处理统一异常的handler
* */
@ControllerAdvice//用于拦截异常
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /*
    * 处理系统异常
    * */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e){
        logger.error("Default Exception",e);
        return ApiRestResponse.error(AlphenMallExceptionEnum.SYS_ERROR);
    }

    /*
    * 处理业务异常
    * */
    @ExceptionHandler(AlphenMallException.class)
    @ResponseBody
    public Object handleAlphenMallException(AlphenMallException e){
        logger.error("AlphenMallException",e);
        return ApiRestResponse.error(e.getCode(), e.getMessage());
    }

    /*
    * 处理参数不符合异常
    * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiRestResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        logger.error("MethodArgumentNotValidException",e);
        return handleBindingResult(e.getBindingResult());
    }
//    把异常处理为对外暴露的提示
    private ApiRestResponse handleBindingResult(BindingResult result){
        List<String> list = new ArrayList<>();
//        若存在错误
        if(result.hasErrors()){
//            将错误都放入list中
            List<ObjectError> allError = result.getAllErrors();
            for(ObjectError objectError:allError){
                String message = objectError.toString();
//                将错误文本放入list
                list.add(message);
            }
        }
//        若错误文本list中没有值,则是参数本身的错误比如类型不对
        if(list.size() == 0){
            return ApiRestResponse.error(AlphenMallExceptionEnum.REQUEST_PARAM_ERROR);
        }
        return ApiRestResponse.error(AlphenMallExceptionEnum.REQUEST_PARAM_ERROR.getCode(),list.toString());
    }
}
