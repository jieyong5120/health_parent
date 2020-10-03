package com.ssm.dao;

import com.github.pagehelper.Page;
import com.ssm.pojo.Setmeal;

import java.util.List;
import java.util.Map;

/*
 * 套餐持久层接口
 * */
public interface SetmealDao {

    //根据前台输入的查询条件,分页查询
    Page<Setmeal> selectByCondition(String queryString);

    //新增套餐
    void add(Setmeal setmeal);

    //中间表,套餐与检查组的对应关系(新增套餐和编辑套餐共用)
    void setSetmealAndCheckGroup(Map<String, Integer> map);

    //获取所有套餐信息
    List<Setmeal> findAll();

    //根据id查询套餐详情,包含套餐基本信息、套餐对应的检查组信息以及检查组对应的检查项信息,一并都查询出来
    Setmeal findById(int id);

    //查询体检套餐种类的数量
    List<Map<String, Object>> findSetmealCount();
}
