package com.ssm.service;

import com.ssm.entity.CheckItemDeleteFailException;
import com.ssm.entity.PageResult;
import com.ssm.pojo.CheckItem;

import java.util.List;

/*
 * 检查项服务接口
 * */
public interface CheckItemService {

    //新增检查项
    void add(CheckItem checkItem);

    //分页查询
    PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString);

    //删除检查项
    void delete(Integer id) throws CheckItemDeleteFailException;

    //根据id查询检查项
    CheckItem findById(Integer id);

    //编辑检查项
    void edit(CheckItem checkItem);

    //查询所有检查项
    List<CheckItem> findAll();
}
