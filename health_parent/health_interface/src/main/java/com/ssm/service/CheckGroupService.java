package com.ssm.service;

import com.ssm.entity.PageResult;
import com.ssm.pojo.CheckGroup;

import java.util.List;

/*
 * 检查组服务接口
 * */
public interface CheckGroupService {

    //新增检查组
    void add(CheckGroup checkGroup, Integer[] checkitemIds);

    //分页查询
    PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString);

    //根据id查询检查组
    CheckGroup findById(Integer id);

    //根据检查组的id查询对应关联的所有检查项
    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    //编辑检查组
    void edit(CheckGroup checkGroup, Integer[] checkitemIds);

    //删除检查组(清除中间关系表)
    void delete(Integer id);

    //查询所有检查组(用于展示在新增(以及编辑)套餐窗口检查组列表的表格中)
    List<CheckGroup> findAll();

}
