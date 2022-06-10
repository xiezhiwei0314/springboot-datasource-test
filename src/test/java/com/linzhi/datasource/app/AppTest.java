package com.linzhi.datasource.app;

import static org.junit.Assert.assertTrue;

import com.linzhi.datasource.app.dao.SysUserMapper;
import com.linzhi.datasource.app.domain.entity.SysUser;
import com.linzhi.datasource.app.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class AppTest 
{

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private IUserService userService;

    @Test
    public void findUserByIdTest(){
        SysUser sysUser = sysUserMapper.selectUserById(1l);
        System.out.println(sysUser.getUserId()+" " +sysUser.getUserName()+"  "+sysUser.getNickName());
    }

    @Test
    public void findUserAllTest(){

        //SysUser sysUser =  userService.selectUserById(1l);
        List<SysUser> sysUser =  userService.selectAllUserInfo();
        sysUser.forEach(user->{
            System.out.println(user.getUserId()+" " +user.getUserName()+"  "+user.getNickName());
        });

    }


}
