package com.linzhi.datasource.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author linzhi
 * @Description 响应基类
 * @Date 2022/06/08 11:30
 */
@Data
public class BaseResponseDTO<T> implements Serializable {

    private static final long serialVersionUID = -3486791899509356039L;

    /**
     * 接口响应成功
     */
    public static final String SUCCESS = "000000";
    /**
     * 接口处理
     */
    public static final String MSG = "交易成功";

    /**
     * 返回码
     */
    private String code;
    /**
     * 返回信息
     */
    private String msg;
    /**
     * 业务数据
     */
    private T data;

    public static <T> BaseResponseDTO<T> build(T data) {
        return new BaseResponseDTO<>(data);
    }

    /**
     * 接口响应是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return BaseResponseDTO.SUCCESS.equals(this.code);
    }

    public BaseResponseDTO() {
    }

    public BaseResponseDTO(T data) {
        this.code = BaseResponseDTO.SUCCESS;
        this.msg = BaseResponseDTO.MSG;
        this.data = data;
    }

    public BaseResponseDTO(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BaseResponseDTO(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
