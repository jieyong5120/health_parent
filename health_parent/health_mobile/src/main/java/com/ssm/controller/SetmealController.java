package com.ssm.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ssm.constant.MessageConstant;
import com.ssm.entity.Result;
import com.ssm.pojo.Setmeal;
import com.ssm.service.SetmealService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * 套餐管理(移动端)
 * */
@RestController  //注解相当于@Controller注解,还加上了一个@ResponseBody注解
@RequestMapping("/setmeal")
public class SetmealController {

    //套餐业务层对象
    @Reference  //查找服务,从服务注册中心zookeeper获取服务
    private SetmealService setmealService;

    //获取所有套餐信息
    @RequestMapping("/getSetmeal")
    public Result getSetmeal() {
        try {
            List<Setmeal> list = setmealService.findAll();
            return new Result(true, MessageConstant.GET_SETMEAL_LIST_SUCCESS, list);  //查询套餐列表成功
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_SETMEAL_LIST_FAIL);  //查询套餐列表失败
        }
    }

    //根据id查询套餐详情,包含套餐基本信息、套餐对应的检查组信息以及检查组对应的检查项信息,一并都查询出来
    @RequestMapping("/findById")
    public Result findById(int id) {
        try {
            Setmeal setmeal = setmealService.findById(id);
            return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS, setmeal);  //查询套餐数据成功
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_SETMEAL_FAIL);  //查询套餐数据失败
        }
    }

}
