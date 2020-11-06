package com.fox.spider.stock.entity.po.nets;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 网易实时交易分钟交易数据
 *
 * @author lusongsong
 * @date 2020/11/6 15:28
 */
@Data
public class NetsRealtimeMinuteNodeDataPo {
    /**
     * 分钟小时时间
     */
    String time;
    /**
     * 价格
     */
    BigDecimal price;
    /**
     * 均价
     */
    BigDecimal avgPrice;
    /**
     * 成交量
     */
    Long dealNum;
}
