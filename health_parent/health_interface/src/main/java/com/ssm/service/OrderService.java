package com.ssm.service;

import com.ssm.entity.Result;

import java.util.Map;

/*
 * 预约服务接口
 * */
public interface OrderService {

    //体检预约
    Result order(Map map) throws Exception;

    //根据预约id查询预约相关信息(包括套餐信息和会员信息)
    Map findById(Integer id) throws Exception;
}
