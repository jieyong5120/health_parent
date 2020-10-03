package com.ssm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ssm.dao.CheckGroupDao;
import com.ssm.entity.PageResult;
import com.ssm.pojo.CheckGroup;
import com.ssm.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 检查组业务
 * */
@Service(interfaceClass = CheckGroupService.class)  //在Service注解中加入interfaceClass属性,作用是指定服务的接口类型
@Transactional  //开启事务
public class CheckGroupServiceImpl implements CheckGroupService {

    //注入检查组持久层对象
    @Autowired
    private CheckGroupDao checkGroupDao;

    @Override  //新增检查组,同时需要设置检查组与检查项的对应关系
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        checkGroupDao.add(checkGroup);
        /*
         * 调用设置检查组与检查项的对应关系的方法(多对多关系)
         *
         * 这里补充一句,该add方法在传递CheckGroup实体类参数时,并没有为CheckGroup实体类的id字段赋值
         *
         * 获取到CheckGroup实体类中的id字段值,实际上是在添加检查组时(通过MyBatis框架提供的selectKey标签获取到自增产生的id值),
         * keyProperty属性作用:执行insert语句将自动分配产生的id(自增主键)的值,封装到CheckGroup实体类中的id字段里面
         * */
        this.setCheckGroupAndCheckItem(checkGroup.getId(), checkitemIds);
    }

    @Override  //分页查询
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage, pageSize);  //MyBatis框架提供的分页助手
        Page<CheckGroup> page = checkGroupDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(), page.getResult());  //通过分页助手计算得到,第一个参数是总记录数(助手内部计算得来),第二个参数是当前页结果
    }

    @Override  //根据id查询检查组
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    @Override  //根据检查组的id查询对应关联的所有检查项
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    @Override  //编辑检查组,同时需要更新和检查项的关联关系
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        //根据检查组id删除中间关系表数据(清理原有关联关系)
        checkGroupDao.deleteAssociation(checkGroup.getId());
        //向中间表中插入数据(建立新的检查组与该检查组关联的检查项的关联关系)
        this.setCheckGroupAndCheckItem(checkGroup.getId(), checkitemIds);
        //更新检查组基本信息
        checkGroupDao.edit(checkGroup);
    }

    @Override  //删除检查组(清除中间关系表)
    public void delete(Integer id) {
        //根据检查组id删除中间关系表数据(清理原有关联关系)
        checkGroupDao.deleteAssociation(id);
        //删除检查组
        checkGroupDao.delete(id);
    }

    @Override  //查询所有检查组(用于展示在新增(以及编辑)套餐窗口检查组列表的表格中)
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

    //设置检查组与检查项的对应关系的方法(多对多关系)----该方法是新增检查组和编辑检查组里面公共调用的方法
    public void setCheckGroupAndCheckItem(Integer checkGroupId, Integer[] checkitemIds) {
        if (checkitemIds != null && checkitemIds.length > 0) {
            for (Integer checkitemId : checkitemIds) {
                Map<String, Integer> map = new HashMap<>(); //自己想了想,记得应该在增强for里面生成一个Map容器
                map.put("checkgroup_id", checkGroupId);
                map.put("checkitem_id", checkitemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }

}
