package com.ssm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.ssm.dao.MemberDao;
import com.ssm.dao.OrderDao;
import com.ssm.service.ReportService;
import com.ssm.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 报表业务(查询运营数据统计业务)
 * */
@Service(interfaceClass = ReportService.class)  //在Service注解中加入interfaceClass属性,作用是指定服务的接口类型
@Transactional  //报表业务,只涉及到查询数据,没有数据的变化,也可以不加事务的注解
public class ReportServiceImpl implements ReportService {

    //注入会员持久层对象
    @Autowired
    private MemberDao memberDao;

    //注入预约持久层对象
    @Autowired
    private OrderDao orderDao;

    @Override  //获得运营统计数据
    public Map<String, Object> getBusinessReport() throws Exception {
        /**
         * 获得运营统计数据
         *
         * Map数据格式:
         *      todayNewMember ‐> number
         *      totalMember ‐> number
         *      thisWeekNewMember ‐> number
         *      thisMonthNewMember ‐> number
         *      todayOrderNumber ‐> number
         *      todayVisitsNumber ‐> number
         *      thisWeekOrderNumber ‐> number
         *      thisWeekVisitsNumber ‐> number
         *      thisMonthOrderNumber ‐> number
         *      thisMonthVisitsNumber ‐> number
         *      hotSetmeal ‐> List<Setmeal>
         */

        //获取当前日期
        String today = DateUtils.parseDate2String(DateUtils.getToday());
        //获取本周一的日期
        String thisWeekMonday = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());
        //获得本月第一天的日期
        String firstDay4ThisMonth = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());

        //查询今日新增会员数
        Integer todayNewMember = memberDao.findMemberCountByDate(today);
        //查询总会员数
        Integer totalMember = memberDao.findMemberTotalCount();
        //查询本周新增会员数(调用方法:根据日期统计会员数,统计指定日期之后的会员数)
        Integer thisWeekNewMember = memberDao.findMemberCountAfterDate(thisWeekMonday);
        //查询本月新增会员数(调用方法:根据日期统计会员数,统计指定日期之后的会员数)
        Integer thisMonthNewMember = memberDao.findMemberCountAfterDate(firstDay4ThisMonth);

        //查询今日预约数
        Integer todayOrderNumber = orderDao.findOrderCountByDate(today);
        //查询本周预约数
        Integer thisWeekOrderNumber = orderDao.findOrderCountAfterDate(thisWeekMonday);
        //查询本月预约数
        Integer thisMonthOrderNumber = orderDao.findOrderCountAfterDate(firstDay4ThisMonth);

        //查询今日就诊数
        Integer todayVisitsNumber = orderDao.findVisitsCountByDate(today);
        //查询本周就诊数
        Integer thisWeekVisitsNumber = orderDao.findVisitsCountAfterDate(thisWeekMonday);
        //查询本月就诊数
        Integer thisMonthVisitsNumber = orderDao.findVisitsCountAfterDate(firstDay4ThisMonth);

        //热门套餐(取前5名)
        List<Map> hotSetmeal = orderDao.findHotSetmeal();

        Map<String, Object> map = new HashMap<>();

        //报表日期
        map.put("reportDate", today);

        //会员数报表数据
        map.put("todayNewMember", todayNewMember);
        map.put("totalMember", totalMember);
        map.put("thisWeekNewMember", thisWeekNewMember);
        map.put("thisMonthNewMember", thisMonthNewMember);

        //已预约报表数据
        map.put("todayOrderNumber", todayOrderNumber);
        map.put("thisWeekOrderNumber", thisWeekOrderNumber);
        map.put("thisMonthOrderNumber", thisMonthOrderNumber);

        //已就诊报表数据
        map.put("todayVisitsNumber", todayVisitsNumber);
        map.put("thisWeekVisitsNumber", thisWeekVisitsNumber);
        map.put("thisMonthVisitsNumber", thisMonthVisitsNumber);

        //热门套餐报表数据(取前5名)
        map.put("hotSetmeal", hotSetmeal);

        return map;
    }
}
