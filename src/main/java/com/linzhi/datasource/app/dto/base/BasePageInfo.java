package com.linzhi.datasource.app.dto.base;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应基类
 *
 * @author linzhi
 * @date 2022/06/08
 */
@Data
public class BasePageInfo<T>  implements Serializable {

    /**
     * 总记录数
     */
    Long totalNum;
    /**
     * 记录页数
     */
    Integer pageNum;
    /**
     * 每页数目
     */
    Integer pageSize;

    /**
     * 记录
     */
    List<T> records;


}
