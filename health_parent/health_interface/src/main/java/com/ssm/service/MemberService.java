package com.ssm.service;

import com.ssm.pojo.Member;

import java.util.List;

/*
* 会员服务接口
* */
public interface MemberService {

    //根据id查询会员(业务场景:手机号快速登录,通过手机号判断当前用户是否是会员)
    Member findByTelephone(String telephone);

    //新增会员,注册为会员
    void add(Member member);

    //根据日期查询当月会员数量
    List<Integer> findMemberCountByMonth(List<String> month);
}
