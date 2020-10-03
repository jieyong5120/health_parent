package com.ssm.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.ssm.constant.MessageConstant;
import com.ssm.constant.RedisMessageConstant;
import com.ssm.entity.Result;
import com.ssm.pojo.Member;
import com.ssm.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/*
 * 会员管理
 * */
@RestController  //注解相当于@Controller注解,还加上了一个@ResponseBody注解
@RequestMapping("/member")
public class MemberController {

    //会员业务层对象
    @Reference  //查找服务,从服务注册中心zookeeper获取服务
    private MemberService memberService;

    //注入Redis连接池对象
    @Autowired
    private JedisPool jedisPool;

    //使用手机号验证码登录
    @RequestMapping("/login")
    public Result login(HttpServletResponse response, @RequestBody Map map) {  //@RequestBody注解处理前台传递来的json数据,没有对应的实体类对应,因此就是Map来接受参数
        //获取前台用户输入的手机号码
        String telephone = (String) map.get("telephone");
        //获取前台用户输入的短信验证码
        String validateCode = (String) map.get("validateCode");

        //从Redis中获取缓存的验证码,key为手机号+RedisMessageConstant.SENDTYPE_LOGIN 手机号+手机号快速登录时发送的验证码的编号
        String codeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);

        //校验手机验证码
        if (codeInRedis == null || !codeInRedis.equals(validateCode)) {
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);  //验证码输入错误
        } else {  //验证码输入正确

            //判断当前用户是否是会员
            Member member = memberService.findByTelephone(telephone);

            if (member == null) {
                //当前用户不是会员,需要添加到会员表
                member = new Member();
                member.setPhoneNumber(telephone);
                member.setRegTime(new Date());

                memberService.add(member);  //新增会员,注册为会员
            }

            /*
             * 登录成功,写入cookie
             *
             * 内容为手机号码,跟踪用户,区分用户的一种手段
             *
             * 登录成功后,在Redis中存储登录信息,而不使用Session,原因是Session不能在Redis部署集群之间共享数据
             * */
            Cookie cookie = new Cookie("login_member_telephone", telephone);
            cookie.setPath("/");  //路径
            cookie.setMaxAge(60 * 60 * 24 * 30);  //有效期30天
            response.addCookie(cookie);  //存入到http的响应对象中,向客户端浏览器写入Cookie

            /*
             * 保存会员信息到Redis中
             *
             * 将member对象序列化为JSON串,Redis中不能直接保存Java对象的
             *
             * 采用阿里巴巴Fastjson:将Java对象转换为JSON格式
             *
             * 网上摘抄:
             *      提供了 toJSONString() 和 parseObject() 方法来将 Java 对象与 JSON 相互转换。
             *      调用toJSONString方 法即可将对象转换成 JSON 字符串,
             *      parseObject 方法则反过来将 JSON 字符串转换成对象。
             * */
            String json = JSON.toJSON(member).toString();  //将Java对象转化为JSON字符串,方便存入到Redis中
            jedisPool.getResource().setex(telephone, 60 * 30, json);  //有效时间30分钟

            return new Result(true, MessageConstant.LOGIN_SUCCESS);  //登录成功
        }
    }
}
