package com.fox.spider.stock.entity.po.sina;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author lusongsong
 * @date 2021/1/11 15:39
 */
@Data
public class SinaBigDealPo implements Serializable {
    /**
     * 股票所属交易所
     */
    Integer stockMarket;
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 时间
     */
    String time;
    /**
     * 价格
     */
    BigDecimal price;
    /**
     * 之前价格
     */
    BigDecimal prePrice;
    /**
     * 成交量
     */
    Long dealNum;
    /**
     * 性质
     */
    Integer dealType;
}
