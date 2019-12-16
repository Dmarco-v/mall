package com.seckillproject.service.impl;

import com.seckillproject.dao.ItemDOMapper;
import com.seckillproject.dao.ItemStockDOMapper;
import com.seckillproject.dataObject.ItemDO;
import com.seckillproject.dataObject.ItemStockDO;
import com.seckillproject.error.BusinessExeption;
import com.seckillproject.error.EmBusinessError;
import com.seckillproject.service.ItemService;
import com.seckillproject.service.PromoService;
import com.seckillproject.service.model.ItemModel;
import com.seckillproject.service.model.PromoModel;
import com.seckillproject.validator.ValidationResult;
import com.seckillproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDOMapper itemDOMapper;
    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private PromoService promoService;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessExeption {
        //入参校验
        ValidationResult result=validator.validate(itemModel);
        if(result.isHasError()){
            throw new BusinessExeption(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        //将Model转为DO，写入数据库
        ItemDO itemDO=convertItemDOFromItemModel(itemModel);
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());
        ItemStockDO itemStockDO=convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);

        //返回创建的对象
        return this.getItemById(itemModel.getId());

    }
    private ItemDO convertItemDOFromItemModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemDO itemDO=new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }
    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemStockDO itemStockDO=new ItemStockDO();
        BeanUtils.copyProperties(itemModel,itemStockDO);
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }


    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList=itemDOMapper.listItem();
        //使用java8 stream api将ItemDO转为ItemModel
        List<ItemModel> itemModelList=itemDOList.stream().map(itemDO->{
            ItemStockDO itemStockDO=itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel=convertModelFromDataObject(itemDO,itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());

        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        //获取商品信息
        ItemDO itemDO=itemDOMapper.selectByPrimaryKey(id);
        if(itemDO==null){
            return null;
        }
        //获取库存信息
        ItemStockDO itemStockDO=itemStockDOMapper.selectByItemId(itemDO.getId());

        ItemModel itemModel=convertModelFromDataObject(itemDO,itemStockDO);

        //获取活动商品信息
        PromoModel promoModel=promoService.getPromoByItemId(itemModel.getId());
        if(promoModel !=null && promoModel.getStatus() !=3){
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }



    private ItemModel convertModelFromDataObject(ItemDO itemDO,ItemStockDO itemStockDO){
        ItemModel itemModel=new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId,Integer amount) {
        //两种方式：1.先查询stock，做比较，再更新；2.直接用where语句判断。
        //第2种性能更好
        int affectedRow=itemStockDOMapper.decreaseStock(itemId,amount);
        if(affectedRow>0){
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessExeption {
        itemDOMapper.increaseSales(itemId,amount);
    }

}
