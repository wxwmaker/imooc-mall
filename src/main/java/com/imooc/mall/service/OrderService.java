package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.model.request.CreateOrderReq;
import com.imooc.mall.model.vo.CartVO;
import java.util.List;

/**
 * 描述：     订单Service
 */
public interface OrderService {


    String create(CreateOrderReq createOrderReq);


}
