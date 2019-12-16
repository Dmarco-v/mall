package com.seckillproject.service;

import com.seckillproject.error.BusinessExeption;
import com.seckillproject.service.model.OrderModel;

public interface OrderService {
    // 1.通过前端url传来秒杀id，然后下单内进行校验对应id是否属于对应商品且活动已经开始。
    // 2.直接在下单接口判断对应商品是否存在秒杀活动，若存在则以秒杀价格下单。
    // 两种方式推荐使用第一种，第二种中下单接口对于没有秒杀活动的商品也要判断，降低访问性能。
    OrderModel createOrder(Integer userId,Integer itemId,Integer promoId, Integer amount) throws BusinessExeption;
}
