package com.mmall.dao;

import com.mmall.pojo.PayInfo;

public interface PayInfoMapper {
    int insert(PayInfo record);

    int insertSelective(PayInfo record);
}