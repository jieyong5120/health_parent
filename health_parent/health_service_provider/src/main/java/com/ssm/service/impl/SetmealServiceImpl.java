package com.ssm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ssm.constant.RedisConstant;
import com.ssm.dao.SetmealDao;
import com.ssm.entity.PageResult;
import com.ssm.pojo.Setmeal;
import com.ssm.service.SetmealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 套餐业务
 * */
@Service(interfaceClass = SetmealService.class)  //在Service注解中加入interfaceClass属性,作用是指定服务的接口类型
@Transactional  //开启事务
public class SetmealServiceImpl implements SetmealService {

    //注入套餐持久层对象
    @Autowired
    private SetmealDao setmealDao;

    //注入Jedis连接池对象
    @Autowired
    private JedisPool jedisPool;

    //注入Freemarker配置对象
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    //加载属性文件,读取属性文件中要生成的html文件所在目录
    @Value("${out_put_path}")
    private String outputpath;

    @Override  //分页查询
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage, pageSize);  //MyBatis框架提供的分页助手
        Page<Setmeal> page = setmealDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(), page.getResult());  //通过分页助手计算得到,第一个参数是总记录数(助手内部计算得来),第二个参数是当前页结果
    }

    @Override  //新增套餐,同时需要设置套餐与检查组的对应关系
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        /*
         * 这里补充一句,该add方法在传递Setmeal实体类参数时,并没有为Setmeal实体类的id字段赋值
         *
         * 获取到Setmeal实体类中的id字段值,实际上是在添加检查组时(通过MyBatis框架提供的selectKey标签获取到自增产生的id值),
         * keyProperty属性作用:执行insert语句将自动分配产生的id(自增主键)的值,封装到Setmeal实体类中的id字段里面
         * */
        if (checkgroupIds != null && checkgroupIds.length > 0) {
            this.setSetmealAndCheckGroup(setmeal.getId(), checkgroupIds);
        }

        //调用将图片名称保存到Redis的方法
        this.savePicFromRedis(setmeal.getImg());

        //当添加套餐后需要重新生成静态页面(套餐列表页面,套餐详情页面)
        this.generateMobileStaticHtml();
    }

    @Override  //获取所有套餐信息
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    @Override  //根据id查询套餐详情,包含套餐基本信息、套餐对应的检查组信息以及检查组对应的检查项信息,一并都查询出来
    public Setmeal findById(int id) {
        return setmealDao.findById(id);
    }

    @Override  //查询体检套餐种类的数量
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }

    //生成当前方法所需的静态页面
    public void generateMobileStaticHtml() {
        //在生成静态页面之前要查询数据
        List<Setmeal> list = this.findAll();

        //需要生成套餐列表静态页面
        generateMobileSetmealListHtml(list);

        //需要生成套餐详情静态页面
        generateMobileSetmealDetailHtml(list);
    }

    //生成套餐列表静态页面
    public void generateMobileSetmealListHtml(List<Setmeal> setmealList) {

        Map map = new HashMap<>();
        map.put("setmealList", setmealList);
        this.generateHtml("mobile_setmeal.ftl", "m_setmeal.html", map);
    }

    //生成套餐详情静态页面(可能有多个)
    public void generateMobileSetmealDetailHtml(List<Setmeal> setmealList) {

        for (Setmeal setmeal : setmealList) {

            Map map = new HashMap<>();
            map.put("setmeal", this.findById(setmeal.getId()));
            this.generateHtml("mobile_setmeal_detail.ftl", "setmeal_detail_" + setmeal.getId() + ".html", map);
        }
    }

    //通用的方法,用于生成静态页面
    public void generateHtml(String templateName, String htmlPageName, Map map) {

        Configuration configuration = freeMarkerConfigurer.getConfiguration(); //获取配置对象
        Writer out = null;
        try {
            //加载模板文件
            Template template = configuration.getTemplate(templateName);

            //生成数据  outputpath:加载属性文件,读取属性文件中要生成的html文件所在目录
//            File docFile = new File(outputpath + "\\" + htmlPageName);
//            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));

            //构造输出流
            out = new FileWriter(new File(outputpath + "/" + htmlPageName));
            template.process(map, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //将图片名称保存到Redis的方法,基于Redis的set集合存储
    public void savePicFromRedis(String pic) {

        //在Redis里面存储一份将要保存到MySQL数据库里面的图片名称
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, pic);
    }

    //设置套餐与检查组的对应关系的方法(多对多关系)----该方法是新增套餐和编辑套餐里面公共调用的方法
    public void setSetmealAndCheckGroup(Integer setmealId, Integer[] checkgroupIds) {
        if (checkgroupIds != null && checkgroupIds.length > 0) {
            for (Integer checkgroupId : checkgroupIds) {
                Map<String, Integer> map = new HashMap<>();  //自己想了想,记得应该在增强for里面生成一个Map容器
                map.put("setmeal_id", setmealId);
                map.put("checkgroup_id", checkgroupId);
                setmealDao.setSetmealAndCheckGroup(map);
            }
        }
    }

}
