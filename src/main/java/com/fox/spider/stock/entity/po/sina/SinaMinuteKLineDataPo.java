package com.fox.spider.stock.entity.po.sina;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 分钟粒度成交数据
 *
 * @author lusongsong
 * @date 2020/11/4 16:43
 */
@Data
public class SinaMinuteKLineDataPo {
    /**
     * 日期
     */
    String dt;
    /**
     * 时间
     */
    String time;
    /**
     * 开盘价
     */
    BigDecimal openPrice;
    /**
     * 收盘价
     */
    BigDecimal closePrice;
    /**
     * 最高价
     */
    BigDecimal highestPrice;
    /**
     * 最低价
     */
    BigDecimal lowestPrice;
    /**
     * 成交数量
     */
    Long dealNum;
}
