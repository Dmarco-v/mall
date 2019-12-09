package com.seckillproject.service;

import com.seckillproject.error.BusinessExeption;
import com.seckillproject.service.model.UserModel;

public interface UserService {
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BusinessExeption;
}
