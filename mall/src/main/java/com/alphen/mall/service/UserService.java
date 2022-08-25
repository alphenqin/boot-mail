package com.alphen.mall.service;

import com.alphen.mall.exception.AlphenMallException;
import com.alphen.mall.model.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.security.NoSuchAlgorithmException;


/*
* UserService
* */
public interface UserService {

    /*
    * 获取用户信息
    * */
    public User getUser();

    /*
    * 注册用户
    * */
    public void register(String username,String password) throws AlphenMallException, NoSuchAlgorithmException;

    /*
    * 登录
    * */
    User login(@Param("username") String username, @Param("password") String password) throws AlphenMallException;

    /*
    * 更新个性签名
    * */
    User updateInformation(User user) throws AlphenMallException;

    /*
    * 判断是否为管理员
    * */
    boolean checkAdmin(User user);
}
