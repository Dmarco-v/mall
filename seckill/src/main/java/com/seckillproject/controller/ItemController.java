package com.seckillproject.controller;

import com.seckillproject.controller.viewObject.ItemVO;
import com.seckillproject.error.BusinessExeption;
import com.seckillproject.response.CommonReturnType;
import com.seckillproject.service.ItemService;
import com.seckillproject.service.model.ItemModel;
import com.seckillproject.service.model.PromoModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller("item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    //创建商品
    @RequestMapping(value = "/create",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name="title") String title,
                                       @RequestParam(name="description") String description,
                                       @RequestParam(name="price") BigDecimal price,
                                       @RequestParam(name="stock") Integer stock,
                                       @RequestParam(name="imgUrl") String imgUrl
                                       ) throws BusinessExeption {
        //封装service请求用于创建商品
        ItemModel itemModel=new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);
        ItemModel itemModelForReturn =itemService.createItem(itemModel);
        ItemVO itemVO=convertVOFromModel(itemModel);
        return CommonReturnType.create(itemVO);

    }

    //商品列表页浏览
    @RequestMapping(value = "/list",method = {RequestMethod.GET})  //get请求不需要设置consumes
    @ResponseBody
    public CommonReturnType listItem(){
        List<ItemModel> itemModelList=itemService.listItem();
        List<ItemVO> itemVOList=itemModelList.stream().map(itemModel -> {
            ItemVO itemVO=convertVOFromModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }


    //商品详情页浏览
    @RequestMapping(value = "/get",method = {RequestMethod.GET})  //get请求不需要设置consumes
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id){
        ItemModel itemModel=itemService.getItemById(id);
        ItemVO itemVO=convertVOFromModel(itemModel);
        return CommonReturnType.create(itemVO);
    }


    private ItemVO convertVOFromModel(ItemModel itemModel){
        if(itemModel==null){
            return  null;
        }
        ItemVO itemVO=new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        //判断有没有秒杀活动
        PromoModel promoModel=itemModel.getPromoModel();
        if(promoModel!=null){
            itemVO.setPromoStatus(promoModel.getStatus());
            itemVO.setPromoId(promoModel.getId());
            itemVO.setStartDate(promoModel.getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoPrice(promoModel.getPromoItemPrice());
        }else{
            itemVO.setPromoStatus(0);
        }

        return itemVO;
    }
}
