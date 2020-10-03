package com.ssm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.ssm.constant.MessageConstant;
import com.ssm.dao.MemberDao;
import com.ssm.dao.OrderDao;
import com.ssm.dao.OrderSettingDao;
import com.ssm.entity.Result;
import com.ssm.pojo.Member;
import com.ssm.pojo.Order;
import com.ssm.pojo.OrderSetting;
import com.ssm.service.OrderService;
import com.ssm.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 * 预约服务
 * */
@Service(interfaceClass = OrderService.class)  //在Service注解中加入interfaceClass属性,作用是指定服务的接口类型
@Transactional  //开启事务
public class OrderServiceImpl implements OrderService {

    //注入预约设置持久层对象
    @Autowired
    private OrderSettingDao orderSettingDao;

    //注入会员持久层对象
    @Autowired
    private MemberDao memberDao;

    //注入预约服务持久层对象
    @Autowired
    private OrderDao orderDao;

    @Override  //体检预约
    public Result order(Map map) throws Exception {
        String orderDate = (String) map.get("orderDate");  //获取用户选择的体检日期
        Date date = DateUtils.parseString2Date(orderDate);  //将字符串日期转换为日期Date类型

        //调用方法获取预约设置对象,可以得到预约设置详细信息
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(date);  //返回预约设置对象,获取到指定日期可预约人数和已预约人数

        //判断指定日期是否可以预约(例如:医生休息)
        if (orderSetting == null) {
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);  //所选日期不能进行体检预约
        }

        //检查预约日期预约人数是否已满
        int number = orderSetting.getNumber();  //可预约人数
        int reservations = orderSetting.getReservations();  //已预约人数

        if (reservations >= number) {
            //预约已满,不能预约
            return new Result(false, MessageConstant.ORDER_FULL);  //提示预约已满
        }

        //检查当前用户是否是会员,通过手机号进行判断
        String telephone = (String) map.get("telephone");
        //根据手机号码查询会员
        Member member = memberDao.findByTelephone(telephone);  //返回会员信息

        //防止重复预约(限定同一用户,同一日期,同一套餐)
        int setmealId = Integer.parseInt((String) map.get("setmealId"));  //获取体检套餐id  为了代码复用,提升代码作用域
        if (member != null) {
            Integer memberId = member.getId();  //获取会员id
            //参数memberId:会员id  参数date:预约日期  参数setmealId:预约套餐id
            Order order = new Order(memberId, date, setmealId);  //体检预约对象
            //动态条件查询
            List<Order> list = orderDao.findByCondition(order);  //老师说了其实不需要用List集合,只是让方法更通用一些

            if (list != null && list.size() > 0) {
                //已经完成预约，不能重复预约
                return new Result(false, MessageConstant.HAS_ORDERED);  //提示已经完成预约，不能重复预约
            }
        } else {
            //当前用户不是会员,需要添加到会员表
            member = new Member();
            member.setName((String) map.get("name"));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(new Date());

            memberDao.add(member);  //新增会员,注册为会员
        }

        //可以预约,预约人数加一
        orderSetting.setReservations(orderSetting.getReservations() + 1);
        orderSettingDao.editReservationsByOrderDate(orderSetting);  //更新已预约人数

        //保存预约信息到预约表
        //第一个参数:会员id(通过MyBatis框架提供的selectKey标签获取到自增产生的id值)  第三个参数:预约类型  第四个参数:未就诊  第五个参数:套餐id
        Order order = new Order(member.getId(), date, (String) map.get("orderType"), Order.ORDERSTATUS_NO, setmealId);
        orderDao.add(order);  //新增预约

        /*
         * 通过MyBatis框架提供的selectKey标签获取到自增产生的id值
         *
         * selectKey 获取新增的主键
         *
         * 在调用OrderDao预约服务持久层对象的add()添加方法之后,会返回得到一个自增主键id的值(自定义设置),因此order.getId()可以回去到值
         * */
        return new Result(true, MessageConstant.ORDER_SUCCESS, order.getId());  //提示预约成功
    }

    @Override  //根据预约id查询预约相关信息(包括套餐信息和会员信息)
    public Map findById(Integer id) throws Exception {
        Map map = orderDao.findByIdFromDetail(id);
        if (map != null) {
            //处理日期格式
            Date orderDate = (Date) map.get("orderDate");
            map.put("orderDate", DateUtils.parseDate2String(orderDate));  //将日期Date类型转化为字符串日期(西方类型日期转化为通常日期类型)
        }
        return map;
    }

}
