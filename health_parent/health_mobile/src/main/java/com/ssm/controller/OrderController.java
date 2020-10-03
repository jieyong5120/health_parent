package com.ssm.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.ssm.constant.MessageConstant;
import com.ssm.constant.RedisMessageConstant;
import com.ssm.entity.Result;
import com.ssm.pojo.Order;
import com.ssm.service.OrderService;
import com.ssm.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/*
 * 预约管理
 * */
@RestController  //注解相当于@Controller注解,还加上了一个@ResponseBody注解
@RequestMapping("/order")
public class OrderController {

    //预约业务层对象
    @Reference  //查找服务,从服务注册中心zookeeper获取服务
    private OrderService orderService;

    //注入Redis连接池对象
    @Autowired
    private JedisPool jedisPool;

    //体检预约
    @RequestMapping("/submit")
    public Result submitOrder(@RequestBody Map map) {  //@RequestBody注解处理前台传递来的json数据,没有对应的实体类对应,因此就是Map来接受参数
        //获取前台用户输入的手机号码
        String telephone = (String) map.get("telephone");
        //获取前台用户输入的短信验证码
        String validateCode = (String) map.get("validateCode");

        //从Redis中获取缓存的验证码,key为手机号+RedisMessageConstant.SENDTYPE_ORDER 手机号+体检预约时发送的验证码的编号
        String codeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);

        //校验手机验证码
        if (codeInRedis == null || !codeInRedis.equals(validateCode)) {
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);  //验证码输入错误
        }

        Result result = null;

        try {
            map.put("orderType", Order.ORDERTYPE_WEIXIN);  //设置体检预约类型,微信预约  预约类型分为微信预约和电话预约

            result = orderService.order(map);  //通过Dubbo远程调用服务在线预约业务处理

        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

        if (result.isFlag()) {  //isFlag()方法返回值是flag,能调用方法说明result不为null,通过Dubbo远程调用服务返回Result对象可以判断flag值为true或false
            //预约成功,发送短信验证码
            String orderDate = (String) map.get("orderDate");  //获取用户选择的体检日期
            try {
                SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE, telephone, orderDate);  //调用阿里云短信服务发送短信验证码的工具类
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //根据预约id查询预约相关信息(包括套餐信息和会员信息)
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        try {
            Map map = orderService.findById(id);
            return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS, map);  //查询预约信息成功
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_ORDER_FAIL);  //查询预约信息失败
        }
    }

}
