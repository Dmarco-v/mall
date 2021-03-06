package com.seckillproject.controller;

import com.seckillproject.error.BusinessExeption;
import com.seckillproject.error.EmBusinessError;
import com.seckillproject.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {

    //后端需要消费的contentType的名字，声明为常量，此处使用urlencoded方式
    public static final String CONTENT_TYPE_FORMED="application/x-www-form-urlencoded";

    //定义exceptionhandler解决未被controller层吸收的exception
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex){
        Map<String,Object> responseData=new HashMap<>();
        if(ex instanceof BusinessExeption){
            BusinessExeption businessExeption=(BusinessExeption)ex;
            responseData.put("errCode",businessExeption.getErrCode());
            responseData.put("errMsg",businessExeption.getErrMsg());
        }else{
            responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg",EmBusinessError.UNKNOWN_ERROR.getErrMsg());
        }
        return CommonReturnType.create(responseData,"fail");
    }

}
