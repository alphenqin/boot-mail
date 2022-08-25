package com.alphen.mall.common;

import com.alphen.mall.exception.AlphenMallExceptionEnum;

/*
* 通用统一返回对象
* */
public class ApiRestResponse<T> {
    private Integer status;
    private String msg;
    private T data;

    private static final int OK_CODE = 1000;
    private static final String OK_MSG = "SUCCESS";

    public ApiRestResponse(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public ApiRestResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ApiRestResponse() {
        this(OK_CODE,OK_MSG);
    }

//    成功时
    public static <T> ApiRestResponse<T> success(){
        return new ApiRestResponse<>();
    }

//    成功时带数据
    public static <T> ApiRestResponse<T> success(T result){
        ApiRestResponse<T> apiRestResponse = new ApiRestResponse();
        apiRestResponse.setData(result);
        return apiRestResponse;
    }

    //    失败时
    public static <T> ApiRestResponse<T> error(Integer code,String msg){
        return new ApiRestResponse<>(code,msg);
    }

//    失败时
    public static <T> ApiRestResponse<T> error(AlphenMallExceptionEnum ex){
        return new ApiRestResponse<>(ex.getCode(),ex.getMsg());
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiRestResponse{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
