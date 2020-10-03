package com.ssm.dao;

import com.github.pagehelper.Page;
import com.ssm.pojo.Member;

import java.util.List;

/*
 * 会员持久层接口
 * */
public interface MemberDao {

    //根据手机号码查询会员(业务场景:判断体检预约检查当前用户是否是注册会员)
    Member findByTelephone(String telephone);

    //新增会员(业务场景:体检预约检查当前用户是否是注册会员,若不是会员,就添加会员)
    void add(Member member);

    //根据日期统计会员数,统计指定日期之前的会员数
    Integer findMemberCountBeforeDate(String date);

    //查询今日新增会员数
    Integer findMemberCountByDate(String date);

    //查询总会员数
    Integer findMemberTotalCount();

    //根据日期统计会员数,统计指定日期之后的会员数(查询本周新增会员数以及本月新增会员数)
    Integer findMemberCountAfterDate(String date);


    List<Member> findAll();

    Page<Member> selectByCondition(String queryString);

    void deleteById(Integer id);

    Member findById(Integer id);

    void edit(Member member);

}
