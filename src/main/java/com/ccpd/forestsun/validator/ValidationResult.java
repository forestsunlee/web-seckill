package com.ccpd.forestsun.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author forestsun
 * @date 2018/12/29
 */
public class ValidationResult {
    //校验是否有错
    private boolean hasErrors=false;

    //存放错误信息的map
    private Map<String,String> errorMsgMap = new HashMap<>();

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Map<String, String> getErrorMsgMap() {
        return errorMsgMap;
    }

    public void setErrorMsgMap(Map<String, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }

    //通过格式化字符串信息获取错误结果msg
    public String getErrMsg (){

        String errMsg = StringUtils.join(errorMsgMap.values().toArray(), ",");

        return errMsg;

    }
}
