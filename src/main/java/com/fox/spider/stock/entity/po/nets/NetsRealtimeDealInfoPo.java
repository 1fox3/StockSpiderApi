package com.fox.spider.stock.entity.po.nets;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 网易股票最新交易日交易数据
 *
 * @author lusongsong
 * @date 2021/1/18 17:51
 */
@Data
public class NetsRealtimeDealInfoPo implements Serializable {
    /**
     * 股票交易所
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
     * 当前价格
     */
    BigDecimal currentPrice;
    /**
     * 今日开盘价
     */
    BigDecimal openPrice;
    /**
     * 今日最高价
     */
    BigDecimal highestPrice;
    /**
     * 今日最低价
     */
    BigDecimal lowestPrice;
    /**
     * 昨日收盘价
     */
    BigDecimal preClosePrice;
    /**
     * 价格涨幅
     */
    BigDecimal uptickPrice;
    /**
     * 增长率
     */
    BigDecimal uptickRate;
    /**
     * 竞买价
     */
    BigDecimal competeBuyPrice;
    /**
     * 竞卖价
     */
    BigDecimal competeSellPrice;
    /**
     * 成交股数
     */
    Long dealNum;
    /**
     * 成交金额
     */
    BigDecimal dealMoney;
    /**
     * 排名靠前的5个买方报价
     */
    LinkedHashMap<BigDecimal, Long> buyPriceMap;
    /**
     * 排名靠前的5个卖方报价
     */
    LinkedHashMap<BigDecimal, Long> sellPriceMap;
    /**
     * 当前日期
     */
    String dt;
    /**
     * 当前时间
     */
    String time;
}
