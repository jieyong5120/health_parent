package com.ssm.service;

import com.ssm.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

/*
 * 预约服务接口
 * */
public interface OrderSettingService {

    //添加预约
    void add(List<OrderSetting> list);

    //根据日期查询预约设置数据
    List<Map> getOrderSettingByMonth(String date);  //参考类型2020-7

    //根据指定日期修改可预订人数
    void editNumberByDate(OrderSetting orderSetting);
}


