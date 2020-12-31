package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 腾讯5日分钟K线交易数据
 *
 * @author lusongsong
 * @date 2020/12/28 15:54
 */
@Data
public class TencentFiveDayMinuteKLinePo implements Serializable {
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
     * 最新交易日交易详情
     */
    TencentRealtimeDealInfoPo realtimeDealInfo;
    /**
     * 分钟粒度的成交信息
     */
    List<TencentDayMinKLinePo> klineData;
}
