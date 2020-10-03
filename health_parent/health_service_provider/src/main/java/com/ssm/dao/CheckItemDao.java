package com.ssm.dao;

import com.github.pagehelper.Page;
import com.ssm.pojo.CheckItem;

import java.util.List;

/*
 * 检查项持久层接口
 * */
public interface CheckItemDao {

    //新增检查项方法
    void add(CheckItem checkItem);

    //根据前台输入的查询条件,分页查询方法
    Page<CheckItem> selectByCondition(String queryString);

    //根据检查项id查询中间关系表
    long findCountByCheckItemId(Integer id);

    //根据id删除检查项
    void deleteById(Integer id);

    //根据id查询检查项
    CheckItem findById(Integer id);

    //编辑检查项
    void edit(CheckItem checkItem);

    //查询所有检查项
    List<CheckItem> findAll();

}
