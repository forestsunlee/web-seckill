package com.ccpd.forestsun.model;

import java.math.BigDecimal;

/**
 * @author forestsun
 * @date 2018/12/30
 */
//用户下单的交易模型
public class OrderModel {

    //订单ID
    private String id;

    //用户ID
    private Integer userId;

    //商品ID
    private Integer itemId;

    //购买数量
    private Integer amount;

    //如果非空，则表示是以秒杀的方式下的订单
    private Integer promoId;

    //下单时商品单价,如果promoId非空，则表示秒杀单价
    private BigDecimal itemPrice;

    //订单总金额
    private BigDecimal orderPrice;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }
}
