package com.ssm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ssm.dao.CheckItemDao;
import com.ssm.entity.CheckItemDeleteFailException;
import com.ssm.entity.PageResult;
import com.ssm.pojo.CheckItem;
import com.ssm.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * 检查项服务
 * */
@Service(interfaceClass = CheckItemService.class)  //在Service注解中加入interfaceClass属性,作用是指定服务的接口类型
@Transactional  //开启事务
public class CheckItemServiceImpl implements CheckItemService {

    //注入检查项持久层对象
    @Autowired
    private CheckItemDao checkItemDao;

    @Override  //新增检查项
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override  //分页查询
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        //基于MyBatis的分页助手来实现分页,来帮我们完成limit关键字(这是MySQL数据库方言),分页助手把limit关键字追加到我们写的SQL语句后面
        PageHelper.startPage(currentPage, pageSize);
        Page<CheckItem> page = checkItemDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(), page.getResult());  //通过分页助手计算得到,第一个参数是总记录数(助手内部计算得来),第二个参数是当前页结果
    }

    @Override  //删除检查项
    public void delete(Integer id) throws CheckItemDeleteFailException {
        //判断当前检查项是否与检查组关联(查询检查组与检查项之间的中间关系表,两者多对多关系)
        long count = checkItemDao.findCountByCheckItemId(id);
        if (count > 0) {
            //count > 0 通过使用聚合函数查询中间表,表示之前有关联,不允许检查项被删除
            //抛出一个自定义的异常类,若抛出一个RuntimeException异常,则可能下面deleteById()方法也可能产生RuntimeException异常
            throw new CheckItemDeleteFailException();
        }
        checkItemDao.deleteById(id);
    }

    @Override  //根据id查询检查项
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    @Override  //编辑检查项
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    @Override  //查询所有检查项
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }

}
