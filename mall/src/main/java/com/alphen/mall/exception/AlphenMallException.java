package com.alphen.mall.exception;

/*
* 统一异常
* */
public class AlphenMallException extends RuntimeException{
    private final Integer code;
    private final String message;

    public AlphenMallException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public AlphenMallException(AlphenMallExceptionEnum exceptionEnum) {
        this(exceptionEnum.getCode(),exceptionEnum.getMsg());
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
