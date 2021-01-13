package com.fox.spider.stock.entity.po.sz;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 深证股票K线数据
 *
 * @author lusongsong
 * @date 2021/1/12 17:23
 */
@Data
public class SZKLinePo implements Serializable {
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
     * 分钟粒度的成交信息
     */
    List<SZKLineNodeDataPo> klineData;
}
