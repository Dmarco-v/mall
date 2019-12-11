package com.seckillproject.service.impl;

import com.seckillproject.dao.UserDOMapper;
import com.seckillproject.dao.UserPasswordDOMapper;
import com.seckillproject.dataObject.UserDO;
import com.seckillproject.dataObject.UserPasswordDO;
import com.seckillproject.error.BusinessExeption;
import com.seckillproject.error.EmBusinessError;
import com.seckillproject.service.UserService;
import com.seckillproject.service.model.UserModel;
import com.seckillproject.validator.ValidationResult;
import com.seckillproject.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;
    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {
        //调用userDOMapper获取UserDO
        UserDO userDO=userDOMapper.selectByPrimaryKey(id);
        if(userDO==null){
            return null;
        }
        //通过用户id获取对应的用户加密密码信息
        UserPasswordDO userPasswordDO=userPasswordDOMapper.selectByUserId(userDO.getId());

        return convertFromDataObject(userDO,userPasswordDO);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessExeption {
        if(userModel==null){
            throw new BusinessExeption(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
/*        if(StringUtils.isEmpty(userModel.getName())
                ||userModel.getGender()==null
                ||userModel.getAge()==null
                ||StringUtils.isEmpty(userModel.getTelephone())){
            throw new BusinessExeption(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }*/
        //使用通用校验
        ValidationResult result=validator.validate(userModel);
        if(result.hasError){
            throw new BusinessExeption(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        UserDO userDO=convertFromModel(userModel);
        try{
            userDOMapper.insertSelective(userDO);
        } catch (DuplicateKeyException e) {
            throw new BusinessExeption(EmBusinessError.PARAMETER_VALIDATION_ERROR,"该手机号已注册！");
        }

        UserPasswordDO userPasswordDO=convertPasswordFromModel(userModel,userDO);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return ;
    }

    @Override
    public UserModel validateLogin(String telephone, String encrptPassword) throws BusinessExeption {
        //通过手机获取用户信息
        UserDO userDO=userDOMapper.selectByTelephone(telephone);
        if(userDO==null){
            throw new BusinessExeption(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO=userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel=convertFromDataObject(userDO,userPasswordDO);

        //校验密码是否一致
        if(!StringUtils.equals(encrptPassword,userModel.getEncrptPassword())){
            throw new BusinessExeption(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel,UserDO userDO){
        if(userModel==null){
            return null;
        }
        UserPasswordDO userPasswordDO=new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userDO.getId());
        return userPasswordDO;
    }

    private  UserDO convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserDO userDO=new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO){
        if(userDO==null){
            return null;
        }
        UserModel userModel=new UserModel();
        BeanUtils.copyProperties(userDO,userModel);

        if(userPasswordDO==null){
            return null;
        }
        userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        return userModel;
    }
}
