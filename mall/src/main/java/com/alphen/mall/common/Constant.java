package com.alphen.mall.common;

import com.alphen.mall.exception.AlphenMallException;
import com.alphen.mall.exception.AlphenMallExceptionEnum;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/*
* 存放静态常量
* */
@Component//让spring识别value注解
public class Constant {
//    盐值
    public static final String SALT = "...";
    public static final String ALPHEN_MALL_USER = "alphen_mall_user";

//    上传的图片，保存后的文件地址
    public static String FILE_UPLOAD_DIR;
    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir){
        FILE_UPLOAD_DIR = fileUploadDir;
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc","price asc");
    }

//    商品是否可售
    public interface SaleStatus{
        int NOT_SALE=0;//商品状态为已下架
        int SALE=1;//商品状态为未下架
    }
//    商品是否被勾选
    public interface Cart{
        int UN_CHECKED=0;//商品没有被选中
        int CHECKED=1;//商品被选中
    }

//    当前订单状态
    public enum OrderStatusEnum{
        CANCELED(0,"用户已取消"),
        NOT_PAID(10,"未付款"),
        PAID(20,"已付款"),
        DELIVERED(30,"已发货"),
        FINISHED(40,"交易完成");

        private int code;
        private String value;

        OrderStatusEnum(int code,String value ) {
            this.code = code;
            this.value = value;
        }

        public static OrderStatusEnum code(int code){
            //增强遍历整个枚举，直到找到输入的code与其中的枚举相匹配，并输出该枚举
            for(OrderStatusEnum orderStatusEnum:values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new AlphenMallException(AlphenMallExceptionEnum.NOT_ENUM);
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
