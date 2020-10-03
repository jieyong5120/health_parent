package com.ssm.dao;

import com.ssm.pojo.User;

/*
 * 用户持久层接口
 * */
public interface UserDao {

    //根据用户名查询对应用户信息(包含用户对应的角色,角色对应哪些权限)
    User findByUsername(String username);
}
