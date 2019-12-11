package com.seckillproject.service;

import com.seckillproject.error.BusinessExeption;
import com.seckillproject.service.model.UserModel;

public interface UserService {
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BusinessExeption;

    /**
     *
     * @param telephone 用户注册手机
     * @param encrptPassword 加密后的密码
     * @throws BusinessExeption
     */
    UserModel validateLogin(String telephone,String encrptPassword) throws BusinessExeption;
}
