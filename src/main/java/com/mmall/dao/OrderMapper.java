package com.mmall.dao;

import com.mmall.pojo.Order;

public interface OrderMapper {
    int insert(Order record);

    int insertSelective(Order record);
}