package com.ccpd.forestsun.error;

/**
 * @author forestsun
 * @date 2018/12/27
 */
public interface CommonError {
    int getErrCode();

    String getErrMsg();

    CommonError setErrMsg(String errMsg);

}
