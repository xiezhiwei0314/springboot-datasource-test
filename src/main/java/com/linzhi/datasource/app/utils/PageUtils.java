package com.linzhi.datasource.app.utils;

import com.github.pagehelper.PageInfo;
import com.linzhi.datasource.app.dto.base.BasePageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linzhi
 * @date 2022/06/08
 */
@Slf4j
public class PageUtils {

    public static BasePageInfo renderPageIno(Class cla, PageInfo pageInfo) {

        List qrList = pageInfo.getList();

        List<Object> res = new ArrayList<>(qrList.size());

        qrList.forEach(e -> {
            try {
                Object object = cla.newInstance();
                BeanUtils.copyProperties(e, object);
                res.add(object);
            } catch (Exception e1) {
                log.error("自动封装分页响应对象异常:{}", pageInfo, e1);
            }
        });

        BasePageInfo response = new BasePageInfo();
        response.setPageNum(pageInfo.getPages());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotalNum(pageInfo.getTotal());
        response.setRecords(res);
        return response;
    }

}
