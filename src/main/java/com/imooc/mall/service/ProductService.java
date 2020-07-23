package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.vo.CategoryVO;

import java.util.List;

/**
 * 商品Service
 */
public interface ProductService {

    void add(AddProductReq addProductReq);
}
