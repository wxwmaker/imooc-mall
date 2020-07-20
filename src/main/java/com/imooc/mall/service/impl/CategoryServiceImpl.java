package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.CategoryMapper;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
    CategoryMapper categoryMapper;

  @Override
    public void add(AddCategoryReq addCategoryReq){
        Category category=new Category();
        BeanUtils.copyProperties(addCategoryReq,category); //值从addCategoryReq 拷贝到category
        Category categoryOld= categoryMapper.selectByName(addCategoryReq.getName());
        if (categoryOld!=null){
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);

        }
        int count = categoryMapper.insertSelective(category);
        if(count==0){
            throw  new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
        }


    }
@Override
    public void update(Category updateCategory){
      if (updateCategory.getName()!=null){
          Category categoryOld = categoryMapper.selectByName(updateCategory.getName());
          if (categoryOld!=null && !categoryOld.getId().equals(updateCategory.getId())){
              throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
          }
      }
        int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if(count==0){
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id){
      Category categoryOld=categoryMapper.selectByPrimaryKey(id);
      //查不到记录,无法删除,删除失败
      if(categoryOld==null){
throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);

      }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if(count==0){
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize,"type,order_num");
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo pageInfo = new PageInfo(categoryList);
        return pageInfo;
    }

}
