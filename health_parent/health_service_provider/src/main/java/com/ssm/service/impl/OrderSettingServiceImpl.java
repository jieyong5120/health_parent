package com.ssm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.ssm.dao.OrderSettingDao;
import com.ssm.pojo.OrderSetting;
import com.ssm.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 预约设置业务
 * */
@Service(interfaceClass = OrderSettingService.class)  //在Service注解中加入interfaceClass属性,作用是指定服务的接口类型
@Transactional  //开启事务
public class OrderSettingServiceImpl implements OrderSettingService {

    //注入预约持久层对象
    @Autowired
    private OrderSettingDao orderSettingDao;

    @Override  //添加预约
    public void add(List<OrderSetting> list) {

        if (list != null && list.size() > 0) {
            for (OrderSetting orderSetting : list) {
                //获取封装OrderSetting预约实体类对象中预约日期,检查此数据(日期),是否已经存在
                long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                //已经存在,执行更新操作(实际场景:多次上传文件)
                if (count > 0) {
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                } else {
                    //不存在,执行添加操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }

    @Override  //根据日期查询预约设置数据
    public List<Map> getOrderSettingByMonth(String date) {  //参考类型2020-7

        String dateBegin = date + "-01";  //参考类型2020-7-1
        String dateEnd = date + "-31";  //参考类型2020-7-31
        Map dataMap = new HashMap<>();
        dataMap.put("dateBegin", dateBegin);
        dataMap.put("dateEnd", dateEnd);

        /*
         * 执行持久层方法,查询预约设置数据
         *
         * 我们要分析页面需要的数据格式内容,通过查询数据库返回的数据List并不满足
         * 因此遍历循环,存入到Map中,键值对格式与json相符
         * */
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(dataMap);

        //下面对获取的数据进行处理
        List<Map> data = new ArrayList<>();
        for (OrderSetting orderSetting : list) {

            Map orderSettingMap = new HashMap();

            //Map的键要结合页面需要,要保持一致
            orderSettingMap.put("date", orderSetting.getOrderDate().getDate());  //获取日期数字(几号)
            orderSettingMap.put("number", orderSetting.getNumber());  //获取可预约人数
            orderSettingMap.put("reservations", orderSetting.getReservations());  //已预约人数

            data.add(orderSettingMap);  //List容器里存放每一天预约设置数据
        }
        return data;
    }

    @Override  //根据指定日期修改可预订人数
    public void editNumberByDate(OrderSetting orderSetting) {

        //获取封装OrderSetting预约实体类对象中预约日期,检查此数据(日期),是否已经存在
        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        //已经存在,执行更新操作(实际场景:多次上传文件),现在是根据指定日期修改可预订人数
        if (count > 0) {
            orderSettingDao.editNumberByOrderDate(orderSetting);
        } else {
            //不存在,执行添加操作(补充:修改指定日期可预订人数,可能指定日期当天在批量导入没有导入)
            orderSettingDao.add(orderSetting);
        }
    }

}
