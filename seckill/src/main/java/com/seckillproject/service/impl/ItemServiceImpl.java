package com.seckillproject.service.impl;

import com.seckillproject.dao.ItemDOMapper;
import com.seckillproject.dao.ItemStockDOMapper;
import com.seckillproject.dataObject.ItemDO;
import com.seckillproject.dataObject.ItemStockDO;
import com.seckillproject.error.BusinessExeption;
import com.seckillproject.error.EmBusinessError;
import com.seckillproject.service.ItemService;
import com.seckillproject.service.model.ItemModel;
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
        ItemDO itemDO=itemDOMapper.selectByPrimaryKey(id);
        if(itemDO==null){
            return null;
        }
        ItemStockDO itemStockDO=itemStockDOMapper.selectByItemId(itemDO.getId());

        ItemModel itemModel=convertModelFromDataObject(itemDO,itemStockDO);
        
        return itemModel;
    }
    private ItemModel convertModelFromDataObject(ItemDO itemDO,ItemStockDO itemStockDO){
        ItemModel itemModel=new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }


}