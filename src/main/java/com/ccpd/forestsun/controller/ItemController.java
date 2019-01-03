package com.ccpd.forestsun.controller;

import com.ccpd.forestsun.error.BusinessException;
import com.ccpd.forestsun.model.ItemModel;
import com.ccpd.forestsun.response.CommonReturnType;
import com.ccpd.forestsun.service.ItemService;
import com.ccpd.forestsun.viewObject.ItemVO;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author forestsun
 * @date 2018/12/30
 */
@Controller
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    //创建商品的controller
    @RequestMapping(value = "/docreate", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam("title") String title,
                                       @RequestParam("discription") String discription,
                                       @RequestParam("price") BigDecimal price,
                                       @RequestParam("stock") Integer stock,
                                       @RequestParam("imgUrl") String imgUrl) throws BusinessException {

        //封装service请求，创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setStock(stock);
        itemModel.setDescription(discription);
        itemModel.setImgUrl(imgUrl);
        itemModel.setPrice(price);
        itemModel.setTitle(title);
        ItemModel itemModelReturn = itemService.create(itemModel);
        ItemVO itemVO = this.convertItemVOFromItemModel(itemModelReturn);
        return CommonReturnType.create(itemVO);

    }

    //商品列表浏览
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItemList() {
        List<ItemModel> itemModelList = itemService.listItem();
        //利用java 8 的stream api 将modelList 封装成Volist
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            return this.convertItemVOFromItemModel(itemModel);
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }



    //商品详情页浏览
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name="id") Integer id){
        ItemModel itemModel = itemService.getItemById(id);
        ItemVO itemVO = this.convertItemVOFromItemModel(itemModel);
        return CommonReturnType.create(itemVO);
    }


    //goto create item view
    @RequestMapping("/goCreateItem")
    public String goCreateItem(){
        return "createItem";
    }

    //goto listItem view
    @RequestMapping("/goList")
    public String goList(){
        return "listItem";
    }

    //goto itemDetail view
    @RequestMapping(value = "/goItem", method = {RequestMethod.GET})
    public ModelAndView goItem(@RequestParam(name="id") Integer id ){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("item");
        mv.addObject("id",id);
        return mv;
    }

    private ItemVO convertItemVOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        if(itemModel.getPromoModel() !=null){
            //有正在进行或即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartTime(itemModel.getPromoModel().getStartTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            itemVO.setPromoStatus(3);
        }
        return itemVO;
    }
}
