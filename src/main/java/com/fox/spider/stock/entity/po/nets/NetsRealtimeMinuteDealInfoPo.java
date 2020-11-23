package com.fox.spider.stock.entity.po.nets;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 网易实时交易分钟线图数据
 *
 * @author lusongsong
 * @date 2020/11/6 15:22
 */
@Data
public class NetsRealtimeMinuteDealInfoPo implements Serializable {
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 日期
     */
    String dt;
    /**
     * 线图的点数量
     */
    Integer nodeCount;
    /**
     * 昨日收盘价
     */
    BigDecimal preClosePrice;
    /**
     * 成交股数
     */
    Long dealNum;
    /**
     * 分钟粒度的成交信息
     */
    List<NetsRealtimeMinuteNodeDataPo> klineData;
}
