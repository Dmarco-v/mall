package com.seckillproject.service;

import com.seckillproject.error.BusinessExeption;
import com.seckillproject.service.model.OrderModel;

public interface OrderService {

    OrderModel createOrder(Integer userId,Integer itemId,Integer amount) throws BusinessExeption;
}
