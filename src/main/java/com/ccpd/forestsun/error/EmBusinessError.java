package com.ccpd.forestsun.error;

/**
 * @author forestsun
 * @date 2018/12/27
 */
public enum EmBusinessError implements CommonError{

    //通用错误类型10001
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),

    UNKOWN_ERROR(10002,"未知错误"),
   //20000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001,"用户不存在"),
    //30000开头为用户登陆相关错误定义
    USER_ERROR_LOGIN(30001,"用户手机号或者密码不正确"),
    //40000开头的为交易信息错误
    STOCK_NOT_ENOUGH(40001,"库存不足")
    ;

    private EmBusinessError(int errCode,String errMsg){
        this.errCode=errCode;
        this.errMsg=errMsg;
    }
    private int errCode;

    private String errMsg;

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
public CommonError setErrMsg(String errMsg) {
    this.errMsg=errMsg;
    return this;
}}
