package com.ssm.service;

import com.ssm.entity.PageResult;
import com.ssm.pojo.Setmeal;

import java.util.List;
import java.util.Map;

/*
 * 套餐服务接口
 * */
public interface SetmealService {

    //分页查询
    PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString);

    //新增套餐
    void add(Setmeal setmeal, Integer[] checkgroupIds);

    //获取所有套餐
    List<Setmeal> findAll();

    //根据id查询套餐详情,包含套餐基本信息、套餐对应的检查组信息以及检查组对应的检查项信息,一并都查询出来
    Setmeal findById(int id);

    //查询体检套餐种类的数量
    List<Map<String, Object>> findSetmealCount();
}
