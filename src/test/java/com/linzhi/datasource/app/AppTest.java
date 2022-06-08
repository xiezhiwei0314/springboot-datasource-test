package com.linzhi.datasource.app;

import static org.junit.Assert.assertTrue;

import com.linzhi.datasource.app.dao.SysUserMapper;
import com.linzhi.datasource.app.domain.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;



@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class AppTest 
{

    @Autowired
    private SysUserMapper sysUserMapper;

    @Test
    public void findUserByIdTest(){
        SysUser sysUser = sysUserMapper.selectUserById(1l);
        System.out.println(sysUser.getUserId()+" " +sysUser.getUserName());
    }

    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
}
