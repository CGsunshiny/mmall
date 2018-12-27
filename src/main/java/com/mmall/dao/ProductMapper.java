package com.mmall.dao;

import com.mmall.pojo.Product;

public interface ProductMapper {
    int insert(Product record);

    int insertSelective(Product record);
}