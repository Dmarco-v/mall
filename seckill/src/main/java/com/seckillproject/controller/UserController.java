package com.seckillproject.controller;

import com.seckillproject.controller.viewObject.UserVO;
import com.seckillproject.service.UserService;
import com.seckillproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("user")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/get")
    @ResponseBody
    private UserVO getUser(@RequestParam(name="id") Integer id){
        //调用service服务获取对应id的用户对象返回给前端
        UserModel userModel=userService.getUserById(id);
        return convertFromModel(userModel);
    }

    private UserVO convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserVO userVO= new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }
}
