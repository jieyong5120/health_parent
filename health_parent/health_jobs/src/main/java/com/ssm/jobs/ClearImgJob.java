package com.ssm.jobs;

import com.ssm.constant.RedisConstant;
import com.ssm.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/*
 * 自定义Job类,定时清理垃圾图片
 * */
public class ClearImgJob {

    //注入Jedis连接池对象
    @Autowired
    private JedisPool jedisPool;

    public void clearImg() {
        //根据Redis中保存的两个set集合进行差值计算,获得垃圾图片(在七牛云里面存储,没有保存到MySQL数据库里面)名称集合
        Set<String> setDifference = jedisPool.getResource().sdiff(
                RedisConstant.SETMEAL_PIC_RESOURCES,
                RedisConstant.SETMEAL_PIC_DB_RESOURCES);

        //根据Redis中保存的两个set集合进行交集计算,获得已经上传到MySQL数据库里面的图片(在七牛云里面存储,保存到MySQL数据库里面)名称集合
        Set<String> setIntersection = jedisPool.getResource().sinter(
                RedisConstant.SETMEAL_PIC_RESOURCES,
                RedisConstant.SETMEAL_PIC_DB_RESOURCES);

        if (setDifference != null) {
            for (String picName : setDifference) {
                //删除七牛云里面存储的图片
                QiniuUtils.deleteFileFromQiniu(picName);
                //再把Redis数据库里面删除垃圾图片
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES, picName);
                System.out.println("自定义任务执行,清理垃圾图片:" + picName);
            }

            //差集不为空,交集可能为空(没有图片上传到MySQL数据库)也可能不为空(有图片上传到MySQL数据库)
            //存储到MySQL数据库里面的图片,在业务层也保存到Redis里面,此时可以遍历交集集合,删除七牛云存储的交集集合图片

            //在七牛云里面存储,保存到MySQL数据库里面的图片,也一并删除,降低七牛云存储空间内存占用
            if (setIntersection != null) {
                for (String interPicName : setIntersection) {
                    //删除七牛云里面存储的图片
                    QiniuUtils.deleteFileFromQiniu(interPicName);
                    System.out.println("自定义任务执行,清理上传到MySQL数据库图片:" + interPicName);
                }
            }
        }
    }

}
