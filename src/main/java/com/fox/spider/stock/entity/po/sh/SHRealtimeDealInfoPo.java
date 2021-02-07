package com.fox.spider.stock.entity.po.sh;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;

/**
 * 证股票最新交易数据
 *
 * @author lusongsong
 * @date 2021/1/12 11:25
 */
@Data
public class SHRealtimeDealInfoPo implements Serializable {
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票所属交易所
     */
    Integer stockMarket;
    /**
     * 股票名称
     */
    String StockName;
    /**
     * 日期
     */
    String dt;
    /**
     * 时间
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
     * 涨停价
     */
    BigDecimal upLimitPrice;
    /**
     * 跌停价
     */
    BigDecimal downLimitPrice;
    /**
     * 价格涨幅
     */
    BigDecimal uptickPrice;
    /**
     * 增长率
     */
    BigDecimal uptickRate;
    /**
     * 成交量
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
