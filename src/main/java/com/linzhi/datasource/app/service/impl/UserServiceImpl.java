package com.linzhi.datasource.app.service.impl;

import com.linzhi.datasource.app.dao.SysUserMapper;
import com.linzhi.datasource.app.domain.entity.SysUser;
import com.linzhi.datasource.app.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private SysUserMapper sysUserMapper;


    @Override
    public List<SysUser> selectAllUserInfo() {
        List<SysUser> sysUsers = sysUserMapper.selectAllUserInfo();
        return sysUsers;
    }

    @Override
    public SysUser selectUserById(Long id) {

        SysUser sysUser = sysUserMapper.selectUserById(id);
        return sysUser;
    }
}
