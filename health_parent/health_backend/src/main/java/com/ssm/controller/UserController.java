package com.ssm.controller;

import com.ssm.constant.MessageConstant;
import com.ssm.entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 用户管理
 * */
@RestController  //注解相当于@Controller注解,还加上了一个@ResponseBody注解
@RequestMapping("/user")
public class UserController {

    //获取当前登录用户的用户名  SpringSecurity框架底层依赖于Session
    @RequestMapping("/getUsername")
    public Result getUsername() throws Exception {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  //返回SpringSecurity框架提供的User对象
            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS, user.getUsername());  //获取当前登录用户名称成功
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_USERNAME_FAIL);  //获取当前登录用户名称失败
        }
    }

}
