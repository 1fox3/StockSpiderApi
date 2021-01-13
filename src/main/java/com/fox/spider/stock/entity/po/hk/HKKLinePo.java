package com.fox.spider.stock.entity.po.hk;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 港股K线数据
 *
 * @author lusongsong
 * @date 2021/1/12 19:16
 */
@Data
public class HKKLinePo implements Serializable {
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票所属交易所
     */
    Integer stockMarket;
    /**
     * K线数据
     */
    List<HKKLineNodeDataPo> klineData;
}
