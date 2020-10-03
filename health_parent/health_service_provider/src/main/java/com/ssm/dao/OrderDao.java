package com.ssm.dao;

import com.ssm.pojo.Order;

import java.util.List;
import java.util.Map;

/*
 * 预约持久层接口
 * */
public interface OrderDao {

    //新增预约
    void add(Order order);

    //动态条件查询(业务场景:判断体检预约检查当前用户是否重复预约(指定预约用户,预约日期,预约套餐))
    List<Order> findByCondition(Order order);

    //根据预约id查询预约相关信息(包括套餐信息和会员信息)
    Map findByIdFromDetail(Integer id);

    //根据日期统计预约数
    Integer findOrderCountByDate(String date);

    //根据日期统计预约数,统计指定日期之后的预约数(查询本周已预约人数以及本月已预约人数)
    Integer findOrderCountAfterDate(String date);

    //根据日期统计到诊数
    Integer findVisitsCountByDate(String date);

    //根据日期统计到诊数,统计指定日期之后的到诊数(查询本周已就诊人数以及本月已就诊人数)
    Integer findVisitsCountAfterDate(String date);

    //热门套餐,查询前5条
    List<Map> findHotSetmeal();
}
