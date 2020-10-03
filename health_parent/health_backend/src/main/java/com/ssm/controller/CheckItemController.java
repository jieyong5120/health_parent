package com.ssm.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ssm.constant.MessageConstant;
import com.ssm.entity.CheckItemDeleteFailException;
import com.ssm.entity.PageResult;
import com.ssm.entity.QueryPageBean;
import com.ssm.entity.Result;
import com.ssm.pojo.CheckItem;
import com.ssm.service.CheckItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * 检查项管理
 * */
@RestController //注解相当于@Controller注解,还加上了一个@ResponseBody注解
@RequestMapping("/checkitem")
public class CheckItemController {

    //检查项业务层对象
    @Reference  //查找服务,从服务注册中心zookeeper获取服务
    private CheckItemService checkItemService;

    //新增检查项
    @PreAuthorize("hasAuthority('CHECKITEM_ADD')")  //权限校验
    @RequestMapping("/add")
    public Result add(@RequestBody CheckItem checkItem) {
        //@RequestBody注解不能省略,帮我们把前台的json数据转化为JavaBean对象
        try {
            checkItemService.add(checkItem);
        } catch (Exception e) {
            e.printStackTrace();
            //服务调用失败
            return new Result(false, MessageConstant.ADD_CHECKITEM_FAIL); //新增检查项失败,返回提示信息
        }
        return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);  //新增检查项成功,返回提示信息
    }

    //检查项分页查询
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")  //权限校验
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult pageResult = checkItemService.pageQuery(
                queryPageBean.getCurrentPage(),
                queryPageBean.getPageSize(),
                queryPageBean.getQueryString());
        return pageResult;  //类上加上了@RestController注解,SpringMVC框架就会把JavaBean对象转化为json数据,响应给页面
    }

    //删除检查项
    @PreAuthorize("hasAuthority('CHECKITEM_DELETE')")  //权限校验
    @RequestMapping("/delete")
    public Result delete(Integer id) {
        //方法只有一个参数,就不需要使用@RequestBody注解了
        try {
            checkItemService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof CheckItemDeleteFailException) {
                //检查组与检查项之间有关联,不能删除
                return new Result(false, "当前检查项关联检查组被引用，不能删除");
            }
            //服务调用失败,删除失败,代码逻辑问题
            return new Result(false, MessageConstant.DELETE_CHECKITEM_FAIL);
        }
        return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
    }

    //弹出编辑窗口,查询数据用于回显数据
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        try {
            CheckItem checkItem = checkItemService.findById(id);
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS, checkItem);
        } catch (Exception e) {
            e.printStackTrace();
            // 服务调用失败
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }

    //编辑检查项
    @PreAuthorize("hasAuthority('CHECKITEM_EDIT')")  //权限校验
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckItem checkItem) {
        try {
            checkItemService.edit(checkItem);
        } catch (Exception e) {
            //服务调用失败
            return new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
        }
        return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
    }

    //查询所有检查项(用于展示在新增(以及编辑)检查组窗口检查项列表的表格中)
    @RequestMapping("/findAll")
    public Result findAll() {
        //将查询到的所有的检查项存放在List容器里面
        List<CheckItem> checkItemList = checkItemService.findAll();
        if (checkItemList != null && checkItemList.size() > 0) {
            //将查询到的所有检查项返回给前台
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS, checkItemList);
        }
        return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
    }

}
