package com.ccpd.forestsun.service;

import com.ccpd.forestsun.error.BusinessException;
import com.ccpd.forestsun.model.OrderModel;

/**
 * @author forestsun
 * @date 2019/1/1
 */
public interface OrderService {

    //方式1：通过前端传上来的秒杀活动Id,然后下单接口内校验对应ID是否合法，且活动开始（推荐）
    //方式2：直接在下单接口内判断对应的商品是否存在秒杀活动，若存在且在进行中，则以秒杀价格下单
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException;
}
