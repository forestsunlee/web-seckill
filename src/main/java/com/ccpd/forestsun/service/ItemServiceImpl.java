package com.ccpd.forestsun.service;

import com.ccpd.forestsun.domain.Item;
import com.ccpd.forestsun.domain.ItemStock;
import com.ccpd.forestsun.error.BusinessException;
import com.ccpd.forestsun.error.EmBusinessError;
import com.ccpd.forestsun.mapper.ItemMapper;
import com.ccpd.forestsun.mapper.ItemStockMapper;
import com.ccpd.forestsun.model.ItemModel;
import com.ccpd.forestsun.model.PromoModel;
import com.ccpd.forestsun.validator.ValidationResult;
import com.ccpd.forestsun.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author forestsun
 * @date 2018/12/30
 */

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemStockMapper itemStockMapper;

    @Autowired
    private PromoService promoService;

    @Transactional
    @Override
    public ItemModel create(ItemModel itemModel) throws BusinessException {
        //校验入参数
        ValidationResult result = validator.validate(itemModel);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        //转化itemmodel -> item
        Item item = this.convertItemFromItemModel(itemModel);

        //商品入库
        itemMapper.insertSelective(item);
        itemModel.setId(item.getId());
        ItemStock itemStock = this.convertItemStockFromItemModel(itemModel);
        //库存入库
        itemStockMapper.insertSelective(itemStock);
        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

    //按销量排序获取itemList
    @Override
    public List<ItemModel> listItem() {
        List<Item> itemList = itemMapper.listItem();
        List<ItemModel> itemModelList = itemList.stream().map(item -> {
            ItemStock itemStock = itemStockMapper.selectByItemId(item.getId());
            ItemModel itemModel = this.convertModelFromDataObject(item, itemStock);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        Item item = itemMapper.selectByPrimaryKey(id);
        if(item == null){
            return null;
        }
        //获取库存数量
        ItemStock itemStock = itemStockMapper.selectByItemId(item.getId());

        //将dataObject -> model
        ItemModel itemModel = this.convertModelFromDataObject(item, itemStock);

        //获取活动商品的信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());

        if(promoModel != null && promoModel.getStatus().intValue() !=3){
            itemModel.setPromoModel(promoModel);
        }


        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {

        int affectedRow = itemStockMapper.decreaseStock(itemId, amount);
        if(affectedRow>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) {
    itemMapper.increaseSales(itemId,amount);
    }

    private Item convertItemFromItemModel(ItemModel  itemModel){

        if(itemModel == null){
            return null;
        }

        Item item = new Item();
        BeanUtils.copyProperties(itemModel,item);
        return item;
    }

    private ItemStock convertItemStockFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemStock itemStock = new ItemStock();
        itemStock.setItemId(itemModel.getId());
        itemStock.setStock(itemModel.getStock());
        return itemStock;
    }

    private ItemModel convertModelFromDataObject(Item item,ItemStock itemStock){

        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(item,itemModel);
        itemModel.setStock(itemStock.getStock());
        return itemModel;
    }
}
