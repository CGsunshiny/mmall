package com.mmall.dao;

import com.mmall.pojo.Category;

public interface CategoryMapper {
    int insert(Category record);

    int insertSelective(Category record);
}