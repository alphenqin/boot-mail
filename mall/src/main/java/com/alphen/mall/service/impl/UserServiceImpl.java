package com.alphen.mall.service.impl;

import com.alphen.mall.exception.AlphenMallException;
import com.alphen.mall.exception.AlphenMallExceptionEnum;
import com.alphen.mall.model.dao.UserMapper;
import com.alphen.mall.model.pojo.User;
import com.alphen.mall.service.UserService;
import com.alphen.mall.utils.MD5Utils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /*
     * 获取用户信息
     * */
    @Override
    public User getUser() {
        User user = userMapper.selectByPrimaryKey(2);
        return user;
    }

    /*
     * 注册用户
     * */
    @Override
    public void register(String username, String password) throws AlphenMallException, NoSuchAlgorithmException {
//        查询用户是否存在，不允许重名
        User result = userMapper.selectByName(username);
        if(result != null){
            throw new AlphenMallException(AlphenMallExceptionEnum.NAME_EXISTED);
        }

//        将新用户写入数据库
        User user = new User();
        user.setUsername(username);
        user.setPassword(MD5Utils.getMD5Str(password));
        int count = userMapper.insertSelective(user);
        if(count == 0){
            throw new AlphenMallException(AlphenMallExceptionEnum.INSERT_FAILED);
        }
    }

    /*
    * 登录
    * */
    @Override
    public User login(@Param("username") String username, @Param("password") String password) throws AlphenMallException {
        String md5 = null;
        try {
            md5 = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        User user = userMapper.selectLogin(username,md5);
        if(user == null){
            throw new AlphenMallException(AlphenMallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    /*
    * 更新个性签名
    * */
    @Override
    public User updateInformation(User user) throws AlphenMallException {
        int count = userMapper.updateByPrimaryKeySelective(user);
        if(count > 1){
            throw new AlphenMallException(AlphenMallExceptionEnum.UPDATE_FAILED);
        }
        return user;
    }

    /*
    * 判断是否为管理员
    * */
    @Override
    public boolean checkAdmin(User user){
//        1-普通用户 2-管理员
        return user.getRole().equals(2);
    }
}
