package com.ccpd.forestsun.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author forestsun
 * @date 2018/12/29
 */

@Component
public class ValidatorImpl implements InitializingBean {

    private Validator validator;

    //在spring容器初始化后扫描到该bean后调用该方法
    @Override
    public void afterPropertiesSet() throws Exception {

        //该方法将 hibernate validator 通过工厂模式拿到一个实例并注入到validator属性中
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    //实现校验方法并返回校验结果
    public ValidationResult validate(Object bean){
        final ValidationResult validationResult = new ValidationResult();
        Set<ConstraintViolation<Object>> set = validator.validate(bean);
        if (set.size() > 0) {
            validationResult.setHasErrors(true);
            set.forEach(validationItem -> {
                String errMsg = validationItem.getMessage();
                String propertiesName = validationItem.getPropertyPath().toString();
                validationResult.getErrorMsgMap().put(propertiesName, errMsg);

            });
        }
        return validationResult;
    }
}
