package com.imooc.mall.service.impl;

import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.filter.UserFilter;
import com.imooc.mall.model.dao.CartMapper;
import com.imooc.mall.model.dao.OrderItemMapper;
import com.imooc.mall.model.dao.OrderMapper;
import com.imooc.mall.model.dao.ProductMapper;
import com.imooc.mall.model.pojo.Order;
import com.imooc.mall.model.pojo.OrderItem;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.CreateOrderReq;
import com.imooc.mall.model.vo.CartVO;
import com.imooc.mall.service.CartService;
import com.imooc.mall.service.OrderService;
import com.imooc.mall.util.OrderCodeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    CartService cartService;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CartMapper cartMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;

    //数据库事务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderReq createOrderReq) {
        //拿到用户id
        Integer userId = UserFilter.currentUser.getId();
        //从购物车查找已经勾选的商品
        List<CartVO> cartVOList=cartService.list(userId);
        ArrayList<CartVO> cartVOListTemp = new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO =  cartVOList.get(i);
            if(cartVO.getSelected().equals(Constant.Cart.CHECKED)){ //如果已勾选就保存起来
                cartVOListTemp.add(cartVO);
            }
        }
        cartVOList=cartVOListTemp;
        //如果购物车已勾选的为空,报错
        if(CollectionUtils.isEmpty(cartVOList)){
            throw new ImoocMallException(ImoocMallExceptionEnum.CART_EMPTY);
        }
        //判断商品是否存在,上下架状态,库存
        validSaleStatusAndStock(cartVOList);

        //把购物车对象转为订单item对象
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);

        //扣库存
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem =  orderItemList.get(i);
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
           int stock= product.getStock()-orderItem.getQuantity();
           if(stock<0){
               throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
           }
           product.setStock(stock);
           productMapper.updateByPrimaryKeySelective(product);
        }

        //把购物车中的已勾选商品删除
        cleanCart(cartVOList);

        //生成订单
        Order order = new Order();
        //生成订单号,有独立的规则
        String orderNO=OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNO);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);//包邮
        order.setPaymentType(1);//在线付款
        //插入到order表中
        orderMapper.insertSelective(order);
        //循环保存每个商品到order_item表
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem =  orderItemList.get(i);
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }
//        把结果返回
        return orderNO;

    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice=0;
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem =  orderItemList.get(i);
            totalPrice +=orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    private void cleanCart(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO =  cartVOList.get(i);
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    //cartVOList 是通过userId查出来的
    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        List<OrderItem> orderItemList=new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO =  cartVOList.get(i);
            OrderItem orderItem =new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            //记录商品快照信息
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO =  cartVOList.get(i);
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            //判断商品是否存在,商品是否上架
            if(product==null||product.getStatus().equals(Constant.SaleStatus.NOT_SALE)){
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SALE);
            }
            //判断商品库存
            if(cartVO.getQuantity()>product.getStock()){
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
            }
        }
        

    }
}
