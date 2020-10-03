package com.ssm.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ssm.constant.MessageConstant;
import com.ssm.entity.Result;
import com.ssm.pojo.OrderSetting;
import com.ssm.service.OrderSettingService;
import com.ssm.utils.POIUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 * 预约设置管理
 * */
@RestController  //注解相当于@Controller注解,还加上了一个@ResponseBody注解
@RequestMapping("/ordersetting")
public class OrderSettingController {

    //预约业务层对象
    @Reference  //查找服务,从服务注册中心zookeeper获取服务
    private OrderSettingService orderSettingService;

    //文件上传
    @RequestMapping("/upload")  //将请求参数绑定到控制器的方法参数上(是SpringMVC中接收普通参数的注解)
    public Result upload(@RequestParam("excelFile") MultipartFile excelFile) {

        try {
            //读取Excel表格数据
            List<String[]> list = POIUtils.readExcel(excelFile);
            if (list != null && list.size() > 0) {
                //定义预约套餐容器
                List<OrderSetting> orderSettingList = new ArrayList<>();

                for (String[] strings : list) {
                    //将表格数据封装到OrderSetting预约实体类对象中
                    OrderSetting orderSetting = new OrderSetting(new Date(strings[0]), Integer.parseInt(strings[1])); //参数预约日期orderDate和预约人数reservations
                    //容器存放预约信息
                    orderSettingList.add(orderSetting);
                }

                //调用业务层方法,添加预约
                orderSettingService.add(orderSettingList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            //文件解析失败
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);  //批量导入预约信息失败
        }
        return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);  //批量导入预约信息成功
    }

    //根据日期查询预约设置数据
    @RequestMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String date) {  //参考类型2020-7

        try {
            List<Map> list = orderSettingService.getOrderSettingByMonth(date);
            return new Result(true, MessageConstant.GET_ORDERSETTING_SUCCESS, list);  //获取预约设置数据成功
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_ORDERSETTING_FAIL);   //获取预约设置数据失败
        }
    }

    //根据指定日期修改可预订人数
    @RequestMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting) {  //@RequestBody注解处理前台传递来的json数据

        try {
            orderSettingService.editNumberByDate(orderSetting);
            return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);  //修改预约设置可预订人数成功
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDERSETTING_FAIL);  //修改预约设置可预订人数失败
        }
    }

}
