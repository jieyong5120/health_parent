package com.ssm.dao;

import com.ssm.pojo.Role;

import java.util.Set;

/*
 * 角色持久层接口
 * */
public interface RoleDao {

    //根据用户id查询对应的角色集合(包含角色对应的权限)
    Set<Role> findByUserId(Integer userId);
}
