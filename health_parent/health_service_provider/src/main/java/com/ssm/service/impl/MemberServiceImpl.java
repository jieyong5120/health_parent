package com.ssm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.ssm.dao.MemberDao;
import com.ssm.pojo.Member;
import com.ssm.service.MemberService;
import com.ssm.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/*
 * 会员服务
 * */
@Service(interfaceClass = MemberService.class)  //在Service注解中加入interfaceClass属性,作用是指定服务的接口类型
@Transactional  //开启事务
public class MemberServiceImpl implements MemberService {

    //注入会员持久层对象
    @Autowired
    private MemberDao memberDao;

    @Override  //根据id查询会员(业务场景:手机号快速登录,通过手机号判断当前用户是否是会员)
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    @Override  //新增会员,注册为会员
    public void add(Member member) {
        /*
         * 短信验证码快速登录
         *
         * 注册会员没有设置密码,但也可能在其他业务调用该方法注册成为会员,并设置了密码
         * 希望在数据库里面存入的密码不是明文密码
         * */
        if (member.getPassword() != null) {  //说明用户提交注册信息设置了密码
            //使用MD5将明文密码进行加密
            member.setPassword(MD5Utils.md5(member.getPassword()));  //如果设置了密码就给加密成密文密码
        }

        memberDao.add(member);  //注册成为会员
    }

    @Override  //根据日期查询当月会员数量
    public List<Integer> findMemberCountByMonth(List<String> months) {
        List<Integer> list = new ArrayList<>();

        for (String month : months) {
            month = month + ".31";  //格式：2020.08.31

            //根据日期统计会员数,统计指定日期之前的会员数
            Integer count = memberDao.findMemberCountBeforeDate(month);
            list.add(count);
        }

        return list;
    }
}
