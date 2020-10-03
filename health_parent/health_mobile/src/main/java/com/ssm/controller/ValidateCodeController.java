package com.ssm.controller;

import com.aliyuncs.exceptions.ClientException;
import com.ssm.constant.MessageConstant;
import com.ssm.constant.RedisMessageConstant;
import com.ssm.entity.Result;
import com.ssm.utils.SMSUtils;
import com.ssm.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

/*
 * 短信验证码管理
 * */
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    //注入Redis连接池对象
    @Autowired
    private JedisPool jedisPool;

    //体检预约发送短信验证码
    @RequestMapping("/sendFromOrder")
    public Result sendFromOrder(String telephone) {
        Integer code = ValidateCodeUtils.generateValidateCode(4);  //调用工具类,生成4位短信验证码
        try {
            //发送短信 第一个参数指定发送短信验证码,第二个参数指定手机号码,第三个参数指定要发送短信的随机验证码是什么
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, code.toString());

        } catch (ClientException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);  //验证码发送失败
        }
        System.out.println("发送的手机验证码为:" + code);

        /*
         *将生成的验证码存入到Redis里面,有效期为5分钟
         *
         * setex()方法
         * 命令为指定的 key 设置值及其过期时间。如果 key 已经存在， SETEX 命令将会替换旧的值。
         *
         * RedisMessageConstant.SENDTYPE_ORDER  调用工具类用于缓存体检预约时发送的验证码的编号
         * */
        jedisPool.getResource().setex(telephone + RedisMessageConstant.SENDTYPE_ORDER, 5 * 60, code.toString());  //有效时间5分钟

        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

    //手机快速登录发送短信验证码
    @RequestMapping("/sendFromLogin")
    public Result sendFromLogin(String telephone) {
        Integer code = ValidateCodeUtils.generateValidateCode(6);  //调用工具类,生成4位短信验证码
        try {
            //发送短信 第一个参数指定发送短信验证码,第二个参数指定手机号码,第三个参数指定要发送短信的随机验证码是什么
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephone, code.toString());

        } catch (ClientException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);  //验证码发送失败
        }
        System.out.println("发送的手机验证码为:" + code);

        //将生成的验证码存入到Redis里面  RedisMessageConstant.SENDTYPE_LOGIN为缓存手机号快速登录时发送的验证码的编号
        jedisPool.getResource().setex(telephone + RedisMessageConstant.SENDTYPE_LOGIN, 5 * 60, code.toString());  //有效时间5分钟

        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }
}
