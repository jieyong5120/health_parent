package com.ssm.dao;

import com.ssm.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 * 预约持久层接口
 * */
public interface OrderSettingDao {

    //添加预约
    void add(OrderSetting orderSetting);

    //根据日期更新可预约人数
    void editNumberByOrderDate(OrderSetting orderSetting);

    //根据预约日期查询(返回查询预约人数)
    long findCountByOrderDate(Date orderDate);

    //根据日期查询预约设置数据
    List<OrderSetting> getOrderSettingByMonth(Map dataMap);

    //根据预约日期查询预约信息(返回预约设置对象,获取到指定日期可预约人数和已预约人数)
    OrderSetting findByOrderDate(Date date);

    //根据日期更新已预约人数
    void editReservationsByOrderDate(OrderSetting orderSetting);
}
