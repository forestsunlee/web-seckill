package com.ccpd.forestsun.service;

import com.ccpd.forestsun.domain.UserInfo;
import com.ccpd.forestsun.domain.UserPassword;
import com.ccpd.forestsun.error.BusinessException;
import com.ccpd.forestsun.error.EmBusinessError;
import com.ccpd.forestsun.mapper.UserInfoMapper;
import com.ccpd.forestsun.mapper.UserPasswordMapper;
import com.ccpd.forestsun.model.UserModel;
import com.ccpd.forestsun.validator.ValidationResult;
import com.ccpd.forestsun.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author forestsun
 * @date 2018/12/26
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserPasswordMapper userPasswordMapper;

    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {

        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(id);
        if(userInfo==null){
            return null;
        }
        UserPassword userPassword = userPasswordMapper.selectByUserId(userInfo.getId());
        UserModel userModel = convertFromDataObject(userInfo, userPassword);
        return userModel;
    }
    @Transactional
    @Override
    public void register(UserModel userModel) throws BusinessException {

        if(null == userModel){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //用户注册业务
      /*  if(StringUtils.isEmpty(userModel.getName()) || userModel.getGender()==null
        || userModel.getAge()==0 || StringUtils.isEmpty(userModel.getTelphone())){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);

        }*/

       //用校验框架进行服务端参数校验
        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }
        UserInfo userInfo = convertFromUserModel(userModel);
        try {
            userInfoMapper.insertSelective(userInfo);
            System.out.print(userInfo.getId());
        } catch (DuplicateKeyException ex) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已被注册");
        }
        //传入数据库自动设置的userId
        userModel.setId(userInfo.getId());
        UserPassword userPassword = convertPasswdFromModel(userModel);
        userPasswordMapper.insertSelective(userPassword);
        return;
    }

    @Override
    public UserModel validataLogin(String telphone, String encrpPasswd) throws BusinessException {
        //通过唯一标识的手机号查询出用户的信息
        UserInfo userInfo = userInfoMapper.selectByTelPhone(telphone);
        if(userInfo == null){
        throw new BusinessException(EmBusinessError.USER_ERROR_LOGIN);
        }
        UserPassword userPassword = userPasswordMapper.selectByUserId(userInfo.getId());
        UserModel userModel = convertFromDataObject(userInfo, userPassword);

        // 判断数据库中的加密密码是否和登陆的密码一致
        if(!StringUtils.equals(encrpPasswd,userModel.getEncrptPassword())){
            throw new BusinessException(EmBusinessError.USER_ERROR_LOGIN);
        }
        return userModel;

    }

    private UserModel convertFromDataObject(UserInfo userInfo, UserPassword userPassword){
        if(userInfo == null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userInfo,userModel);
        if(userPassword !=null){

            userModel.setEncrptPassword(userPassword.getEncrptPassword());
        }
        return userModel;

    }

    private UserInfo convertFromUserModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userModel,userInfo);
        return userInfo;

    }

    private UserPassword convertPasswdFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserPassword userPassword = new UserPassword();
        userPassword.setEncrptPassword(userModel.getEncrptPassword());
        userPassword.setUserId(userModel.getId());
        return userPassword;
    }
}
