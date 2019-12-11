package com.seckillproject.controller;

import com.seckillproject.controller.viewObject.UserVO;
import com.seckillproject.dao.UserDOMapper;
import com.seckillproject.dao.UserPasswordDOMapper;
import com.seckillproject.dataObject.UserDO;
import com.seckillproject.dataObject.UserPasswordDO;
import com.seckillproject.error.BusinessExeption;
import com.seckillproject.error.EmBusinessError;
import com.seckillproject.response.CommonReturnType;
import com.seckillproject.service.UserService;
import com.seckillproject.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;//注意此处使用的httpServletRequest是单例线程安全的。

    //用户登录接口
    @RequestMapping(value = "/login",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name="telephone") String telephone,
                                  @RequestParam(name="password") String password) throws BusinessExeption, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                StringUtils.isEmpty(password)){
            throw new BusinessExeption(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //用户登录，校验用户登录是否合法
        UserModel userModel = userService.validateLogin(telephone,this.EncodeByMd5(password));

        //将登陆凭证加入到用户登录成功的session中。此处假设用户单点登录
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        return CommonReturnType.create(null);
    }


    //用户注册接口
    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name="telephone") String telephone,
                                     @RequestParam(name="otpCode") String otpCode,
                                     @RequestParam(name="name") String name,
                                     @RequestParam(name="gender") Integer gender,
                                     @RequestParam(name="age") Integer age,
                                     @RequestParam(name="password") String password) throws BusinessExeption, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的otpCode相符
        String inSessionOtpCode= (String) this.httpServletRequest.getSession().getAttribute(telephone);
        //自带判空处理
        if(!com.alibaba.druid.util.StringUtils.equals(otpCode,inSessionOtpCode)){
            throw new BusinessExeption(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码错误");
        }
        //用户注册流程
        UserModel userModel=new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender)));
        userModel.setAge(age);
        userModel.setTelephone(telephone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.EncodeByMd5(password));
        userService.register(userModel);

        return CommonReturnType.create(null);
    }

    private String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5=MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder=new BASE64Encoder();
        //加密字符串
        String newstr=base64Encoder.encode(md5.digest(str.getBytes(StandardCharsets.UTF_8)));
        return newstr;
    }


    //用户获取otp短信接口
    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telephone") String telephone){
        //按照一定规则生成OTP验证码
        Random random=new Random();
        int randomInt = random.nextInt(999999);
        String otpCode= String.valueOf(randomInt);

        //将OTP验证码与手机号关联，企业一般使用redis处理。在这里使用httpsession的方式进行绑定。
        httpServletRequest.getSession().setAttribute(telephone,otpCode);

        //将OTP验证码通过短信发送给用户。注意企业中开发不可将用户敏感信息打印到控制台，此处是为了便于测试。
        System.out.println("telephone= "+telephone+" otpCode= "+otpCode);

        return CommonReturnType.create(null);
    }

    @RequestMapping("/get")
    @ResponseBody
    private CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BusinessExeption {
        //调用service服务获取对应id的用户对象返回给前端
        UserModel userModel=userService.getUserById(id);
        UserVO userVO=convertFromModel(userModel);

        //获取用户信息不存在异常
        if(userModel==null){
            throw new BusinessExeption(EmBusinessError.USER_NOT_EXIST);
        }

        return CommonReturnType.create(userVO,"success");
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
