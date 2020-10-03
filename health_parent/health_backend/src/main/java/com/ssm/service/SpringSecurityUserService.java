package com.ssm.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ssm.pojo.Permission;
import com.ssm.pojo.Role;
import com.ssm.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component("springSecurityUserService")  //把这个类交给SpringIoC管理
public class SpringSecurityUserService implements UserDetailsService {

    //使用Dubbo通过网络远程调用服务服务提供方获取数据库中的用户信息
    @Reference  //查找服务,从服务注册中心zookeeper获取服务
    private UserService userService;

    @Override  //根据用户名查询数据库获取用户信息
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //远程调用用户服务,根据用户名查询用户信息
        User user = userService.findByUsername(username);

        if (user == null) {
            //根据用户名没有查询到用户,用户不存在
            return null;
        } else {
            List<GrantedAuthority> list = new ArrayList();  //角色权限集合

            //用户对应角色列表,拥有哪些角色
            Set<Role> roles = user.getRoles();
            for (Role role : roles) {
                //授予角色  遍历角色,为用户授予角色
                list.add(new SimpleGrantedAuthority(role.getKeyword()));

                //角色对应权限列表,拥有哪些权限
                Set<Permission> permissions = role.getPermissions();
                for (Permission permission : permissions) {
                    //授予权限  遍历权限,为用户授予权限
                    list.add(new SimpleGrantedAuthority(permission.getKeyword()));  //例如:ROLE_XXX XXX XXX
                }
            }

            //为当前用户授权
            org.springframework.security.core.userdetails.User userDetails
                    = new org.springframework.security.core.userdetails.User(username, user.getPassword(), list);

            return userDetails;
        }

    }
}
