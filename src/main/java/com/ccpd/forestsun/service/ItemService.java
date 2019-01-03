package com.ccpd.forestsun.service;

import com.ccpd.forestsun.error.BusinessException;
import com.ccpd.forestsun.model.ItemModel;

import java.util.List;

/**
 * @author forestsun
 * @date 2018/12/30
 */
public interface ItemService {

    //创建商品
    ItemModel create(ItemModel itemModel) throws BusinessException;

    //商品列表浏览
    List<ItemModel> listItem();

    //商品详情浏览
    ItemModel getItemById(Integer id);

    //库存扣减
    boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException;

    //商品销量增加
    void increaseSales(Integer itemId, Integer amount) throws BusinessException;
}
