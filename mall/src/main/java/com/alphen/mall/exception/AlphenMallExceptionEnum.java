package com.alphen.mall.exception;

/*
* 异常枚举
* */
public enum AlphenMallExceptionEnum {
    NEED_USER_NAME(10001,"用户名不能为空"),
    NEED_PASSWORD(10002,"密码不能为空"),
    PASSWORD_TOO_SHORT(10003,"密码长度不能小于8位"),
    NAME_EXISTED(10004,"不允许重名"),
    INSERT_FAILED(10005,"插入失败，请重试"),
    WRONG_PASSWORD(10006,"密码错误"),
    NEED_LOGIN(10007,"用户未登录"),
    UPDATE_FAILED(10008,"更新失败"),
    NEED_ADMIN(10009,"无管理权限"),
    NAME_NOT_NULL(10010,"名字不能为空"),
    CREATE_FAILED(10011,"创建失败，请重试"),
    REQUEST_PARAM_ERROR(10012,"参数错误"),
    DELETE_FAILED(10013,"删除失败"),
    MKDIR_FAILED(10014,"文件夹创建失败"),
    UPLOAD_FAILED(10015,"图片上传失败"),
    NOT_SALE(10016,"商品状态不可售"),
    NOT_ENOUGH(10017,"商品供应不足"),
    CART_EMPTY(10018,"购物车没有勾选商品"),
    NOT_ENUM(10019,"未找到对应的枚举"),
    NOT_ORDER(10020,"订单不存在"),
    NOT_YOUR_ORDER(10021,"该订单不属于你"),
    WRONG_STATUS_ORDER(10022,"该订单目前状态无法取消"),
    SYS_ERROR(20001,"系统异常");

//    异常码
    private Integer code;
//    异常信息
    private String msg;

    AlphenMallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
