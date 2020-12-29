package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 腾信k线图
 *
 * @author lusongsong
 * @date 2020/12/29 14:20
 */
@Data
public class TencentKLinePo implements Serializable {
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
     * 线图类型
     */
    Integer kLineType;
    /**
     * 线图的点数量
     */
    Integer nodeCount;
    /**
     * 昨日收盘价
     */
    BigDecimal preClosePrice;
    /**
     * 分钟粒度的成交信息
     */
    List<TencentKLineNodeDataPo> klineData;
}
