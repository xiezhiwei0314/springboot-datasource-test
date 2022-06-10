package com.linzhi.datasource.app.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.linzhi.datasource.app.domain.entity.SysUser;
import com.linzhi.datasource.app.dto.base.BasePageInfo;
import com.linzhi.datasource.app.dto.base.Response;
import com.linzhi.datasource.app.dto.request.SysUserRequest;
import com.linzhi.datasource.app.service.IUserService;
import com.linzhi.datasource.app.utils.PageUtils;

//import io.swagger.annotations.ApiOperation;
import com.sun.deploy.net.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Slf4j
//@Controller
@RestController
@RequestMapping("/system/user")
public class SysUserController {


    @Autowired
    private IUserService userService;

    //@PostMapping(value = "/selectUserInfo")
    @RequestMapping(value="selectUserInfo",method = RequestMethod.GET)
    //@ApiOperation("查询所有用户信息")
    public Response<BasePageInfo<SysUser>> selectUserInfo(HttpServletRequest req, HttpServletResponse resp) {
        log.info("查询用信息");
        try {
            SysUserRequest request = new SysUserRequest();
            PageHelper.startPage(request.getPage(), request.getPageSize());
            List<SysUser> querySysUserInfos = userService.selectAllUserInfo();
            PageInfo<SysUser> pageInfo = new PageInfo<>(querySysUserInfos);
            BasePageInfo response = PageUtils.renderPageIno(SysUser.class, pageInfo);
            log.info("查询所有用户信息响应：{}", response);
            return new Response(response);
        }catch (Exception e){
            e.printStackTrace();
            log.info("查询所有用户信息失败:{}",e.getMessage());
        }
        return null;
    }

    @RequestMapping(value="test",method = RequestMethod.GET)
    public String test(){

        return "测试成功";
    }

}
