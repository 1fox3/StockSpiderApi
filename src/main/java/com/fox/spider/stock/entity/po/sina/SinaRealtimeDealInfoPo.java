package com.fox.spider.stock.entity.po.sina;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * 股票实时成交信息
 *
 * @author lusongsong
 * @date 2020/11/5 14:33
 */
@Data
public class SinaRealtimeDealInfoPo implements Serializable {
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
     * 股票英文名
     */
    String stockNameEn;
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
     * 当前分钟最高价
     */
    BigDecimal minuteHighestPrice;
    /**
     * 当前分钟最低价
     */
    BigDecimal minuteLowestPrice;
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
    List<Map<String, BigDecimal>> buyPriceList;
    /**
     * 排名靠前的5个卖方报价
     */
    List<Map<String, BigDecimal>> sellPriceList;
    /**
     * 当前日期
     */
    String dt;
    /**
     * 当前时间
     */
    String time;
    /**
     * 交易状态
     */
    String dealStatus;
    /**
     * 52周最高价
     */
    BigDecimal fiftyTwoWeekHighestPrice;
    /**
     * 51周最低阶
     */
    BigDecimal fiftyTwoWeekLowestPrice;
    /**
     * 股息率
     */
    BigDecimal dividendYield;
    /**
     * 未知的数据列表
     */
    List<String> unknownKeyList;

    /**
     * 获取波动
     *
     * @return
     */
    public BigDecimal getSurgeRate() {
        if (null == preClosePrice || null == highestPrice || null == lowestPrice
                || 0 == preClosePrice.compareTo(BigDecimal.ZERO)
                || 0 == highestPrice.compareTo(BigDecimal.ZERO)
                || 0 == lowestPrice.compareTo(BigDecimal.ZERO)
        ) {
            return BigDecimal.ZERO;
        }
        //波动
        BigDecimal surgeRate = highestPrice.subtract(lowestPrice)
                .multiply(new BigDecimal(100))
                .divide(preClosePrice, 4, RoundingMode.HALF_UP);
        return surgeRate;
    }
}
