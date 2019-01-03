package com.ccpd.forestsun.controller;

import com.ccpd.forestsun.error.BusinessException;
import com.ccpd.forestsun.error.EmBusinessError;
import com.ccpd.forestsun.model.OrderModel;
import com.ccpd.forestsun.model.UserModel;
import com.ccpd.forestsun.response.CommonReturnType;
import com.ccpd.forestsun.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author forestsun
 * @date 2019/1/1
 */

@Controller
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest request;
//封装下单请求
@RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
@ResponseBody
public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId, @RequestParam(name = "promoId", required = false) Integer promoId, @RequestParam(name = "amount") Integer amount)
        throws BusinessException {

    //获取用户的登陆信息
    String is_login =(String) request.getSession().getAttribute("IS_LOGIN");
    if(is_login == null || !is_login.equals("TRUE")){
      throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户未登陆");
     }
    UserModel user = (UserModel)request.getSession().getAttribute("LOGIN_USER");

    OrderModel orderModel = orderService.createOrder(user.getId(), itemId,promoId, amount);

    return CommonReturnType.create(null);

}


}
