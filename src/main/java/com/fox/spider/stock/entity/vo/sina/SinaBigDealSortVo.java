package com.fox.spider.stock.entity.vo.sina;

import lombok.Data;

import java.io.Serializable;

/**
 * 新浪股票交易日大单交易排序
 *
 * @author lusongsong
 * @date 2021/1/11 16:10
 */
@Data
public class SinaBigDealSortVo implements Serializable {
    /**
     * 需要排序的属性
     */
    String column = "ticktime";
    /**
     * 是否为升序排序
     */
    Integer asc = 0;
}
