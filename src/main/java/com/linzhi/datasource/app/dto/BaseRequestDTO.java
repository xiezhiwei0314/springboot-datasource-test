package com.linzhi.datasource.app.dto;

import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * @Author linzhi
 * @Description 基础请求
 * @Date 2022/06/08 11:14
 */
@Data
public class BaseRequestDTO<T> implements Serializable {

    private static final long serialVersionUID = 9183549589007155952L;

    /**
     * 业务数据
     */
    @Valid
    private T data;

}
