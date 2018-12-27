package com.mmall.dao;

import com.mmall.pojo.Shipping;

public interface ShippingMapper {
    int insert(Shipping record);

    int insertSelective(Shipping record);
}