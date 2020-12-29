package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 腾讯按天分钟K线交易数据
 *
 * @author lusongsong
 * @date 2020/12/28 15:57
 */
@Data
public class TencentDayMinKLinePo implements Serializable {
    /**
     * 日期
     */
    String dt;
    /**
     * 昨日收盘价
     */
    BigDecimal preClosePrice;
    /**
     * 线图的点数量
     */
    Integer nodeCount;
    /**
     * 分钟粒度的成交信息
     */
    List<TencentRealtimeMinuteNodeDataPo> klineData;
}
