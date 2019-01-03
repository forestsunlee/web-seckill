package com.ccpd.forestsun.controller;

import com.alibaba.druid.util.StringUtils;
import com.ccpd.forestsun.error.BusinessException;
import com.ccpd.forestsun.error.EmBusinessError;
import com.ccpd.forestsun.model.UserModel;
import com.ccpd.forestsun.response.CommonReturnType;
import com.ccpd.forestsun.service.MailService;
import com.ccpd.forestsun.service.UserService;
import com.ccpd.forestsun.viewObject.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author forestsun
 * @date 2018/12/26
 */
@Controller
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")          //js跨域支持 session共享
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private MailService mailService;

    //用户注册接口
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam("telphone") String telphone, @RequestParam("passwd") String passwd) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验   服务端必须进行参数校验
        if (StringUtils.isEmpty(telphone) || StringUtils.isEmpty(passwd)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //用户登陆合法性校验
        UserModel userModel = userService.validataLogin(telphone, this.encodeByMd5(passwd));

        //通过合法性校验后，将凭证加入session内
        this.request.getSession().setAttribute("IS_LOGIN", "TRUE");
        this.request.getSession().setAttribute("LOGIN_USER", userModel);
        return CommonReturnType.create(null);
    }


    //用户注册接口
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam("telphone") String telphone,
                                     @RequestParam("otpCode") String optCode,
                                     @RequestParam("name") String name,
                                     @RequestParam("gender") Byte gender,
                                     @RequestParam("age") Integer age,
                                     @RequestParam("passwd") String passwd) throws
            BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和otpCode相符合
        String inSessionOtpCode = (String) this.request.getSession().getAttribute(telphone);
        if (!StringUtils.equals(optCode, inSessionOtpCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "无效的短信验证码");
        }
        //用户注册流程
        UserModel userModel = new UserModel();
        userModel.setAge(age);
        userModel.setGender(gender);
        userModel.setName(name);
        userModel.setRegisterMode("byPhone");
        userModel.setTelphone(telphone);
        userModel.setEncrptPassword(this.encodeByMd5(passwd));

        userService.register(userModel);
        return CommonReturnType.create(null);


    }

    //用户获取短信OTP的接口

    @RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOTP(@RequestParam(name = "telphone") String telphone) throws MessagingException {
        //按照一定的规则生成OTP短信验证码
        Random random = new Random();
        int randomNum = random.nextInt(99999);
        randomNum += 10000;
        String otpCode = String.valueOf(randomNum);

        //将OTP验证码与用户手机绑定,此处是用httpsession
        /*注意：此处虽然是使用spring 容器注入的httpsesion,但是它不是单例模式，
        它是经过包装的，是多例子模式，在每个线程中都会创建一个*/
        request.getSession().setAttribute(telphone, otpCode);
        request.getSession().setAttribute("IS_GETOTP", "true");

        //将OTP验证码通过短信通道发送给用户手机或邮箱
        mailService.sendTextMails(telphone,"注册验证","你的验证码为："+otpCode);

        //发送带附件的邮件
   /*     List<String> fileList = new ArrayList<String>();
        fileList.add("E:\\useFile\\sum_dashbd.sql");
        mailService.sendAttachmentMail(telphone,"注册码","你的验证码为："+otpCode,fileList);*/

        return CommonReturnType.create(null);
    }


    @RequestMapping("/getuser")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        //调用service服务获取对应Id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        //如果获取的对应用户信息不存在
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        UserVO userVO = convertFromModel(userModel);
        //返回通用对象
        CommonReturnType commonReturnType = CommonReturnType.create(userVO);
        return commonReturnType;
    }


    @RequestMapping("/index")
    public String index() {
        return "getop";
    }

    @RequestMapping("/goRegister")
    public String goRegister() {
        if (this.request.getSession().getAttribute("IS_GETOTP") != null) {
            return "register";
        } else {
            return "getop";
        }
    }

    @RequestMapping("/goLogin")
    public String goLogin() {
                return "login";
    }

    @RequestMapping("/goSeckill")
    public String goSeckill() {
        return "seckill";
    }

    private UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO uservo = new UserVO();
        BeanUtils.copyProperties(userModel, uservo);
        return uservo;
    }

    private String encodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder encoder = new BASE64Encoder();
        String encode = encoder.encode(md5.digest(str.getBytes("utf-8")));
        return encode;

    }

}
