package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;

import java.io.Serializable;

/**
 * 腾讯相关股票
 *
 * @author lusongsong
 * @date 2020/12/29 17:59
 */
@Data
public class TencentRelateStockPo implements Serializable {
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
}
