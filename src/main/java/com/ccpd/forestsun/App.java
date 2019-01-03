package com.ccpd.forestsun;

import com.ccpd.forestsun.domain.UserInfo;
import com.ccpd.forestsun.mapper.UserInfoMapper;
import com.ccpd.forestsun.mapper.UserPasswordMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring 自动化配置启动器
 *
 */
@SpringBootApplication(scanBasePackages = {"com.ccpd.forestsun"})
@RestController
@MapperScan("com.ccpd.forestsun.mapper")
public class App {

@Autowired
private UserInfoMapper userInfoMapper;

@Autowired
private UserPasswordMapper userPasswordMapper;
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        }

@RequestMapping("/")
public String home() {
    UserInfo userInfo = userInfoMapper.selectByPrimaryKey(1);
    if(userInfo == null){
        return "用户对象不存在";
    }else{
        return userInfo.getName();
    }
        }
        }
