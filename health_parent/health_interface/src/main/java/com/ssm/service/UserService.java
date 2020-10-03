package com.ssm.service;

import com.ssm.pojo.User;

/*
 * 用户服务接口
 * */
public interface UserService {

    //根据用户名查询对应用户信息(包含用户对应的角色,角色对应哪些权限)
    User findByUsername(String username);
}
