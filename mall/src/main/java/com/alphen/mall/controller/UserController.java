package com.alphen.mall.controller;

import com.alphen.mall.common.ApiRestResponse;
import com.alphen.mall.common.Constant;
import com.alphen.mall.exception.AlphenMallException;
import com.alphen.mall.exception.AlphenMallExceptionEnum;
import com.alphen.mall.model.pojo.User;
import com.alphen.mall.service.UserService;
import com.mysql.cj.util.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

/*
* 用户控制器
* */
@Controller
@MapperScan(basePackages = "com.alphen.mall.model.dao")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/test")
    @ResponseBody
    public User personalPage(){
        return userService.getUser();
    }

    /*
    * 注册
    * */
    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("username") String username, @RequestParam("password") String password) throws AlphenMallException, NoSuchAlgorithmException {
        if(StringUtils.isNullOrEmpty(username)){
            return ApiRestResponse.error(AlphenMallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isNullOrEmpty(password)){
            return ApiRestResponse.error(AlphenMallExceptionEnum.NEED_PASSWORD);
        }
        if(password.length()<8){
            return ApiRestResponse.error(AlphenMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(username,password);
        return ApiRestResponse.success();
    }

    /*
    * 登录
    * */
    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) throws AlphenMallException, NoSuchAlgorithmException {
        if(StringUtils.isNullOrEmpty(username)){
            return ApiRestResponse.error(AlphenMallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isNullOrEmpty(password)){
            return ApiRestResponse.error(AlphenMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(username,password);
//        为了安全，将传出去的对象密码设置为null
        user.setPassword(null);
        session.setAttribute(Constant.ALPHEN_MALL_USER,user);
        return ApiRestResponse.success(user);
    }

    /*
    * 更新个性签名
    * */
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session,@RequestParam String signature) throws AlphenMallException {
        User currentUser = (User) session.getAttribute(Constant.ALPHEN_MALL_USER);
        if(currentUser == null){
            throw new AlphenMallException(AlphenMallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }

    /*
    * 注销
    * */
    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session){
        session.removeAttribute(Constant.ALPHEN_MALL_USER);
        return ApiRestResponse.success();
    }

    /*
    * 管理员登录
    * */
    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse loginAdmin(@RequestParam String username,@RequestParam String password,HttpSession session) throws AlphenMallException {
        if(StringUtils.isNullOrEmpty(username)){
            return ApiRestResponse.error(AlphenMallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isNullOrEmpty(password)){
            return ApiRestResponse.error(AlphenMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(username,password);
        if(userService.checkAdmin(user)){
//            是管理员，则执行操作
            user.setPassword(null);
            session.setAttribute(Constant.ALPHEN_MALL_USER,user);
            return ApiRestResponse.success(user);
        }else {
            throw new AlphenMallException(AlphenMallExceptionEnum.NEED_ADMIN);
        }
    }
}
