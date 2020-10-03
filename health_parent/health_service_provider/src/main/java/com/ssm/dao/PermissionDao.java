package com.ssm.dao;

import com.ssm.pojo.Permission;

import java.util.Set;

/*
 * 权限持久层接口
 * */
public interface PermissionDao {

    //根据角色id查询对应的权限集合
    Set<Permission> findByRoleId(Integer roleId);
}
