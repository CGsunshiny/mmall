package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.springframework.stereotype.Repository;

@Repository
public interface CartMapper {
    int insert(Cart record);

    int insertSelective(Cart record);
}