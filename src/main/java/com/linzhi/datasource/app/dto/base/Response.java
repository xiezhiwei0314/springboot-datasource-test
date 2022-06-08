package com.linzhi.datasource.app.dto.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description dubbo响应基类
 * @author linzhi
 * @date 2022/06/08
 */
@Data
public class  Response<T> implements Serializable {


    private static final long serialVersionUID = -3486791899509356039L;

    /**
     * 接口响应成功
     */
    public static final String SUCCESS = "000000";
    /**
     * 接口处理
     */
    public static final String MSG = "接口处理成功";

    /**
     * 返回码
     */
    private String code;
    /**
     * 返回信息
     */
    private String msg;
    /**
     * 具体相应参数
     */
    private T data;

    /**
     * 接口响应是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return Response.SUCCESS.equals(this.code);
    }

    public Response() {
    }

    public Response(T data) {
        this.code = Response.SUCCESS;
        this.msg = Response.MSG;
        this.data = data;
    }

    public Response(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Response(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
