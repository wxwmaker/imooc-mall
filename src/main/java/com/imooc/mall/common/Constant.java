package com.imooc.mall.common;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 描述：     常量值
 */
@Component
public class Constant {

    public static final String IMOOC_MALL_USER = "imooc_mall_user";
    public static final String SALT = "8svbsvjkweDF,.03[";

    public static String ICODE;

    @Value("${icode}")
    public void setICODE(String icode) {
        ICODE = icode;
    }

    public static String FILE_UPLOAD_DIR;
    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir){
        FILE_UPLOAD_DIR=fileUploadDir;
    }

    public interface ProductListOrderBy{
       Set<String> PRICE_ASC_DESC= Sets.newHashSet("price desc","price asc");
    }

    public interface SaleStatus{
        int NOT_SALE=0;//商品下架状态
        int SALE=1;//商品上架状态
    }

    public interface Cart{
        int UN_CHECKED=0;//商品未选中
        int CHECKED=1;//商品选中了
    }

}
