package com.ssm.dao;

import com.github.pagehelper.Page;
import com.ssm.pojo.CheckGroup;

import java.util.List;
import java.util.Map;

/*
 * 检查组持久层接口
 * */
public interface CheckGroupDao {

    //新增检查组
    void add(CheckGroup checkGroup);

    //中间表,检查组与检查项的对应关系(新增检查组和编辑检查组共用)
    void setCheckGroupAndCheckItem(Map<String, Integer> map);

    //根据前台输入的查询条件,分页查询
    Page<CheckGroup> selectByCondition(String queryString);

    //根据id查询检查组
    CheckGroup findById(Integer id);

    //根据检查组的id查询对应关联的所有检查项
    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    //根据检查组id删除中间关系表数据(清理原有关联关系)
    void deleteAssociation(Integer id);

    //更新检查组基本信息
    void edit(CheckGroup checkGroup);

    //删除检查组(清除中间关系表)
    void delete(Integer id);

    //查询所有检查组(用于展示在新增(以及编辑)套餐窗口检查组列表的表格中)
    List<CheckGroup> findAll();

}
