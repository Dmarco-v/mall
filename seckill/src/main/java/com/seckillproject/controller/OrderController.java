package com.seckillproject.controller;

import com.seckillproject.error.BusinessExeption;
import com.seckillproject.error.EmBusinessError;
import com.seckillproject.response.CommonReturnType;
import com.seckillproject.service.OrderService;
import com.seckillproject.service.model.OrderModel;
import com.seckillproject.service.model.UserModel;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //封装下单请求
    @RequestMapping(value = "/create",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name="itemId") Integer itemId,
                                        @RequestParam(name="amount") Integer amount,
                                        @RequestParam(name="promoId",required = false) Integer promoId) throws BusinessExeption {
        //校验用户是否登录
        Boolean isLogin= (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin==null || !isLogin.booleanValue()){
            throw new BusinessExeption(EmBusinessError.USER_NOT_LOGIN);
        }
        //获取用户的登录信息
        UserModel userModel= (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        OrderModel orderModel = orderService.createOrder(userModel.getId(),itemId,promoId,amount);


        return CommonReturnType.create(orderModel);

    }

}
