package com.ccpd.forestsun.response;

import jdk.net.SocketFlow;

/**
 * @author forestsun
 * @date 2018/12/27
 */
public class CommonReturnType {
    //表明对应请求的返回处理结果 "success" 或 "fail"
    private String status;

    //如果status=success,则data内返回前端需要的json数据

    //如果status=fail，则data内返回通用的错误码格式

    private Object data;

    //定义一个通用的创建方法
    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");
    }

    public static CommonReturnType create(Object result,String status){
        CommonReturnType commonReturnType = new CommonReturnType();
        commonReturnType.setStatus(status);
        commonReturnType.setData(result);
        return commonReturnType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
