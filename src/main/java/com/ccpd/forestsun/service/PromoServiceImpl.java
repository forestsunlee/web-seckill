package com.ccpd.forestsun.service;

import com.ccpd.forestsun.domain.Promo;
import com.ccpd.forestsun.mapper.PromoMapper;
import com.ccpd.forestsun.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author forestsun
 * @date 2019/1/1
 */

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoMapper promoMapper;

    //获取即将进行的，或正在进行的秒杀活动
    @Override
    public PromoModel getPromoByItemId(Integer itemId) {

        Promo promo = promoMapper.selectByItemId(itemId);

        PromoModel promoModel = convertFromPromoDataObject(promo);

        if(promoModel == null){
            return null;
        }

        //判断当前时间是否有秒杀活动正在进行或即将进行
        DateTime now = new DateTime();
        if(promoModel.getStartTime().isAfter(now)) {
            promoModel.setStatus(1);
        }else if(promoModel.getEndTime().isBefore(now)){
            promoModel.setStatus(3);
        }else{
            promoModel.setStatus(2);
        }

        return promoModel;
    }

    private PromoModel convertFromPromoDataObject(Promo promo){
        if(promo == null){
            return null;
    }
    PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promo,promoModel);
        promoModel.setStartTime(new DateTime(promo.getStartTime()));
        promoModel.setEndTime(new DateTime(promo.getEndTime()));
        return promoModel;
    }
}
