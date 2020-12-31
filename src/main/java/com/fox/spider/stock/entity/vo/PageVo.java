package com.fox.spider.stock.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页对象信息
 *
 * @author lusongsong
 * @date 2020/12/31 16:26
 */
@Data
@AllArgsConstructor
public class PageVo implements Serializable {
    /**
     * 页码
     */
    Integer pageNum = 0;
    /**
     * 条数
     */
    Integer pageSize = 20;
}
