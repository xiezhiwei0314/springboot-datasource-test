package com.linzhi.datasource.app.service;

import com.linzhi.datasource.app.domain.entity.SysUser;
import com.linzhi.datasource.app.dto.base.Response;

import java.util.List;

public interface IUserService {

    public List<SysUser> selectAllUserInfo();

    public SysUser selectUserById(Long id);
}
