package com.seckillproject.service.impl;

import com.seckillproject.dao.PromoDOMapper;
import com.seckillproject.dataObject.PromoDO;
import com.seckillproject.service.PromoService;
import com.seckillproject.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PromoServiceImpl implements PromoService {
    @Autowired
    private PromoDOMapper promoDOMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        //获取对应商品的秒杀活动信息
        PromoDO promoDO=promoDOMapper.selectByItemId(itemId);
        PromoModel promoModel=convertFromDataObject(promoDO);
        if(promoModel==null){
            return null;
        }

        //判断当前时间内活动的状态
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);
        }else promoModel.setStatus(2);


        return promoModel;
    }

    private PromoModel convertFromDataObject(PromoDO promoDO){
        if(promoDO==null){
            return null;
        }
        PromoModel promoModel=new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
