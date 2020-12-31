package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 腾讯分钟粒度线图信息
 *
 * @author lusongsong
 * @date 2020/12/28 16:59
 */
@Data
public class TencentMinuteScopeKLinePo implements Serializable {
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
     * 线图的点数量
     */
    Integer nodeCount;
    /**
     * 昨日收盘价
     */
    BigDecimal preClosePrice;
    /**
     * 最新交易日交易详情
     */
    TencentRealtimeDealInfoPo realtimeDealInfo;
    /**
     * 分钟粒度的成交信息
     */
    List<TencentMinuteScopeNodeDataPo> klineData;
}
