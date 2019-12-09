package com.seckillproject;

import com.seckillproject.dao.UserDOMapper;
import com.seckillproject.dataObject.UserDO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {"com.seckillproject"})
@RestController
@MapperScan("com.seckillproject.dao")
public class App 
{
    @Autowired
    private UserDOMapper userDOMapper;

    @RequestMapping("/")
    public String home(){
        UserDO userDO=userDOMapper.selectByPrimaryKey(1);
        if(userDO==null){
            return "对象用户不存在";
        }else {
            return userDO.getName();
        }
    }

    public static void main( String[] args )
    {
        SpringApplication.run(App.class,args);
    }
}
