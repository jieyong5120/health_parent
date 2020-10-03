package com.ssm.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ssm.constant.MessageConstant;
import com.ssm.entity.PageResult;
import com.ssm.entity.QueryPageBean;
import com.ssm.entity.Result;
import com.ssm.pojo.CheckGroup;
import com.ssm.service.CheckGroupService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * 检查组管理
 * */
@RestController  //注解相当于@Controller注解,还加上了一个@ResponseBody注解
@RequestMapping("/checkgroup")
public class CheckGroupController {

    //检查组业务层对象
    @Reference  //查找服务,从服务注册中心zookeeper获取服务
    private CheckGroupService checkGroupService;

    //新增检查组
    @RequestMapping("/add")  //方法第二个参数checkitemIds:新增表单中检查项对应的复选框,是一个数组,参数名需保证与请求参数的键保持一致,不然请求不到
    public Result add(@RequestBody CheckGroup checkGroup, Integer[] checkitemIds) {  //@RequestBody注解处理前台传递来的json数据
        try {
            checkGroupService.add(checkGroup, checkitemIds);
        } catch (Exception e) {
            return new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
        }
        return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }

    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult pageResult = checkGroupService.pageQuery(
                queryPageBean.getCurrentPage(),
                queryPageBean.getPageSize(),
                queryPageBean.getQueryString());
        return pageResult;  //类上加上了@RestController注解,SpringMVC框架就会把JavaBean对象转化为json数据,响应给页面
    }

    //根据id查询检查组
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        CheckGroup checkGroup = checkGroupService.findById(id);
        if (checkGroup != null) {  //查询检查组成功
            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, checkGroup);
        }
        return new Result(false, MessageConstant.QUERY_CHECKGROUP_FAIL);
    }

    //根据检查组的id查询对应关联的所有检查项
    @RequestMapping("/findCheckItemIdsByCheckGroupId")
    public Result findCheckItemIdsByCheckGroupId(Integer id) {
        //将查询到的对应关联的所有检查项,存放在List容器里面
        try {
            List<Integer> checkitemIds = checkGroupService.findCheckItemIdsByCheckGroupId(id);
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS, checkitemIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }

    //编辑检查组
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckGroup checkGroup, Integer[] checkitemIds) {  //@RequestBody注解处理前台传递来的json数据
        try {
            checkGroupService.edit(checkGroup, checkitemIds);
        } catch (Exception e) {
            return new Result(false, MessageConstant.EDIT_CHECKGROUP_FAIL);
        }
        return new Result(true, MessageConstant.EDIT_CHECKGROUP_SUCCESS);
    }

    //删除检查组(课件没有)
    @RequestMapping("/delete")
    public Result delete(Integer id) {
        //方法只有一个参数,就不需要使用@RequestBody注解了
        try {
            checkGroupService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.DELETE_CHECKGROUP_FAIL);
        }
        return new Result(true, MessageConstant.DELETE_CHECKGROUP_SUCCESS);
    }

    //查询所有检查组(用于展示在新增(以及编辑)套餐窗口检查组列表的表格中)
    @RequestMapping("/findAll")
    public Result findAll() {
        //将查询到的所有的检查组存放在List容器里面
        List<CheckGroup> checkGroupList = checkGroupService.findAll();
        if (checkGroupList != null && checkGroupList.size() > 0) {
            //将查询到的所有检查组返回给前台
            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, checkGroupList);
        }
        return new Result(false, MessageConstant.QUERY_CHECKGROUP_FAIL);
    }

}
