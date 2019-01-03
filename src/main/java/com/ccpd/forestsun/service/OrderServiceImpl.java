package com.ccpd.forestsun.service;

import com.ccpd.forestsun.domain.OrderInfo;
import com.ccpd.forestsun.domain.SequenceInfo;
import com.ccpd.forestsun.error.BusinessException;
import com.ccpd.forestsun.error.EmBusinessError;
import com.ccpd.forestsun.mapper.OrderInfoMapper;
import com.ccpd.forestsun.mapper.SequenceInfoMapper;
import com.ccpd.forestsun.model.ItemModel;
import com.ccpd.forestsun.model.OrderModel;
import com.ccpd.forestsun.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author forestsun
 * @date 2019/1/1
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private SequenceInfoMapper sequenceInfoMapper;

    @Transactional
    @Override
    public OrderModel createOrder(Integer userId, Integer itemId,Integer promoId, Integer amount) throws BusinessException {

        //校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
        }
        UserModel userModel = userService.getUserById(userId);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户不存在");
        }
        if(amount<=0 || amount>99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"购买数量不正确");
        }

        //校验活动信息
        if(promoId !=null){
            //校验对应活动是否存在这个适用商品
            if(promoId.intValue() != itemModel.getPromoModel().getId()){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
            }else if(itemModel.getPromoModel().getStatus().intValue() !=2){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动还未开始");
            }
        }


        //落单减库存
        boolean result = itemService.decreaseStock(itemId, amount);
        if(!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setItemId(itemId);
        orderModel.setUserId(userId);
        orderModel.setAmount(amount);
        if(promoId != null){
        orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        orderModel.setPromoId(promoId);
        }else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));
        //生成交易的订单号
        orderModel.setId(generateOrderNo());
        OrderInfo orderInfo = convertFromOrderModel(orderModel);
        orderInfoMapper.insertSelective(orderInfo);
        itemService.increaseSales(itemId,amount);

        //返回前端

        return orderModel;
    }

    private OrderInfo convertFromOrderModel(OrderModel orderModel){
        if(orderModel == null){
            return null;
        }
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderModel,orderInfo);
        return orderInfo;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)   //此处开启一个新的事务，表示无论外部事务是否成功，此事务都提交
    protected String generateOrderNo(){
        //订单号16位
        StringBuilder stringBuilder =new StringBuilder();
        //前8位 时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(date);

        //中间6位自增序列
        //获取当前sequence
        int  currentValue =0;
        SequenceInfo sequence = sequenceInfoMapper.getSequenceByName("order_info");
        currentValue = sequence.getCurrentValue();
        Integer step = sequence.getStep();
        sequence.setCurrentValue(currentValue+step);
        sequenceInfoMapper.updateByPrimaryKey(sequence);
        String value = String.valueOf(currentValue);
        for (int i = 0; i <6-value.length() ; i++) {
            stringBuilder.append("0");
        }
        stringBuilder.append(value);

        //最后2位，分库分表位（可以用userId号对一个固定的数进行取模,然后根据余数去判断到底该存放到哪一个数据库中的订单表）
        //暂时省略
        stringBuilder.append("00");

        return stringBuilder.toString();
    }
}
