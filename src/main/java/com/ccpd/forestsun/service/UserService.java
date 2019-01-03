package com.ccpd.forestsun.service;

import com.ccpd.forestsun.error.BusinessException;
import com.ccpd.forestsun.model.UserModel;

/**
 * @author forestsun
 * @date 2018/12/26
 */
public interface UserService {
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BusinessException;
    UserModel validataLogin(String telphone, String encrptPasswd) throws BusinessException;
}
