package com.ssm.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ssm.constant.MessageConstant;
import com.ssm.constant.RedisConstant;
import com.ssm.entity.PageResult;
import com.ssm.entity.QueryPageBean;
import com.ssm.entity.Result;
import com.ssm.pojo.Setmeal;
import com.ssm.service.SetmealService;
import com.ssm.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.UUID;

/*
 * 套餐管理
 * */
@RestController  //注解相当于@Controller注解,还加上了一个@ResponseBody注解
@RequestMapping("/setmeal")
public class SetmealController {

    //套餐业务层对象
    @Reference  //查找服务,从服务注册中心zookeeper获取服务
    private SetmealService setmealService;

    //注入Jedis连接池对象
    @Autowired
    private JedisPool jedisPool;

    //图片上传,将图片信息提交到七牛云服务器,不操作数据库
    @RequestMapping("/upload")  //前台请求在模板上传组件里面  方法参数MultipartFile,需在Spring的配置文件导入文件上传jar包
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile) {
        //获取原始文件名
        String originalFilename = imgFile.getOriginalFilename();
        //获取原始文件名后缀中间的点号索引位置
        int lastIndexOf = originalFilename.lastIndexOf(".");
        //获取文件后缀名
        String suffix = originalFilename.substring(lastIndexOf - 1);
        //使用UUID随机产生文件名称,防止同名文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;  //上传文件的文件名

        //调用七牛云工具类,上传文件
        try {
            QiniuUtils.uploadFileFromQiniu(imgFile.getBytes(), fileName);  //上传会存在异常

            //将上传图片的名称存储到Redis里面(保存一份已经上传到七牛云里面的图片名称),基于Redis的set集合存储
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES, fileName);

            return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS, fileName);  //图片上传成功,回显信息
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);  ////图片上传失败,回显信息
        }
    }

    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult pageResult = setmealService.pageQuery(
                queryPageBean.getCurrentPage(),
                queryPageBean.getPageSize(),
                queryPageBean.getQueryString());
        return pageResult;  //类上加上了@RestController注解,SpringMVC框架就会把JavaBean对象转化为json数据,响应给页面
    }

    //新增套餐
    @RequestMapping("/add")  //方法第二个参数checkgroupIds:新增表单中检查项对应的复选框,是一个数组,参数名需保证与请求参数的键保持一致,不然请求不到
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds) {  //@RequestBody注解处理前台传递来的json数据
        try {
            setmealService.add(setmeal, checkgroupIds);
        } catch (Exception e) {
            return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
        }
        return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
    }

}
