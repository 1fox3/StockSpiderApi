package com.fox.spider.stock.entity.po.sh;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 上证股票最新交易日分钟线图数据
 *
 * @author lusongsong
 * @date 2021/1/12 10:46
 */
@Data
public class SHRealtimeMinuteKLinePo implements Serializable {
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票所属交易所
     */
    Integer stockMarket;
    /**
     * 日期
     */
    String dt;
    /**
     * 时间
     */
    String time;
    /**
     * 上个交易日收盘价
     */
    BigDecimal preClosePrice;
    /**
     * 最高价
     */
    BigDecimal highestPrice;
    /**
     * 最低价
     */
    BigDecimal lowestPrice;
    /**
     * 分钟点数
     */
    Integer nodeCount;
    /**
     * 分钟粒度的成交信息
     */
    List<SHRealtimeMinuteNodeDataPo> klineData;
}
