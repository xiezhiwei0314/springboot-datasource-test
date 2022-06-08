package com.linzhi.datasource.app.dto.base;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数基类
 * @author linzhi
 * @date 2022/06/08
 */
@Data
public class BasePageRequest implements Serializable {

    /**
     * 当前页码
     */
    Integer page = 0;

    /**
     *  每页记录条数
     */
    Integer pageSize = 20;

}
