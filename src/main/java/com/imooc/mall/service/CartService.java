package com.imooc.mall.service;

import com.imooc.mall.model.vo.CartVO;
import org.springframework.stereotype.Service;

import java.util.List;

//购物车service实现类
@Service
public interface CartService {


    List<CartVO> list(Integer userId);

    List<CartVO> add(Integer userId, Integer productId, Integer count);

    List<CartVO> update(Integer userId, Integer productId, Integer count);

    List<CartVO> delete(Integer userId, Integer productId);

    List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected);

    List<CartVO> selectAllOrNot(Integer userId, Integer selected);
}
