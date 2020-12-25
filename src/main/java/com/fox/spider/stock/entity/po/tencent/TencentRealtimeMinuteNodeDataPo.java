package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 腾讯实时交易分钟交易数据
 *
 * @author lusongsong
 * @date 2020/12/25 14:58
 */
@Data
public class TencentRealtimeMinuteNodeDataPo implements Serializable {
    /**
     * 分钟小时时间
     */
    String time;
    /**
     * 价格
     */
    BigDecimal price;
    /**
     * 成交量
     */
    Long dealNum;
}
