package com.fox.spider.stock.entity.po.ifeng;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.TreeMap;
import java.util.List;

/**
 * 凤凰网股票实时成交信息
 *
 * @author lusongsong
 * @date 2021/1/4 15:38
 */
@Data
public class IFengRealtimeDealInfoPo implements Serializable {
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
     * 当前日期
     */
    String dt;
    /**
     * 当前时间
     */
    String time;
    /**
     * 当前价格
     */
    BigDecimal currentPrice;
    /**
     * 今日开盘价
     */
    BigDecimal openPrice;
    /**
     * 均价
     */
    BigDecimal avgPrice;
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
    TreeMap<BigDecimal, Long> buyPriceMap;
    /**
     * 排名靠前的5个卖方报价
     */
    TreeMap<BigDecimal, Long> sellPriceMap;
    /**
     * 未知的数据列表
     */
    List<String> unknownKeyList;
}
