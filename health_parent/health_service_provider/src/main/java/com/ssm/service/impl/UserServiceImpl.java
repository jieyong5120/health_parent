package com.ssm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.ssm.dao.PermissionDao;
import com.ssm.dao.RoleDao;
import com.ssm.dao.UserDao;
import com.ssm.pojo.Permission;
import com.ssm.pojo.Role;
import com.ssm.pojo.User;
import com.ssm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/*
 * 用户服务业务
 * */
@Service(interfaceClass = UserService.class)  //在Service注解中加入interfaceClass属性,作用是指定服务的接口类型
@Transactional
public class UserServiceImpl implements UserService {

    //注入用户持久层对象
    @Autowired
    private UserDao userDao;

    //注入角色持久层对象
    @Autowired
    private RoleDao roleDao;

    //注入权限持久层对象
    @Autowired
    private PermissionDao permissionDao;

    @Override
    public User findByUsername(String username) {

        //根据用户名查询对应用户信息(包含用户对应的角色,角色对应哪些权限)
        User user = userDao.findByUsername(username);  //单独查询不包含对应角色和权限

        System.out.println(user);
        if (user == null) {
            //根据用户名没有查询到用户,用户不存在
            return null;
        }

        Integer userId = user.getId();
        //根据用户id查询对应的角色集合(包含角色对应的权限)
        Set<Role> roles = roleDao.findByUserId(userId);  //单独查询,不包含对应权限

        if (roles != null && roles.size() > 0) {
            for (Role role : roles) {

                Integer roleId = role.getId();
                //根据角色id查询对应的权限集合
                Set<Permission> permissions = permissionDao.findByRoleId(roleId);

                if (permissions != null && permissions.size() > 0) {

                    //角色分配权限
                    role.setPermissions(permissions);
                }
            }

            //用户分配角色
            user.setRoles(roles);
        }

        return user;
    }
}
