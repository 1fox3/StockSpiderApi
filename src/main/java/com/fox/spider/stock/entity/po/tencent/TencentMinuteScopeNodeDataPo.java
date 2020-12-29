package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 腾讯分钟粒度交易数据
 * @author lusongsong
 * @date 2020/12/28 16:59
 */
@Data
public class TencentMinuteScopeNodeDataPo implements Serializable {
    /**
     * 日期
     */
    String dt;
    /**
     * 时间
     */
    String time;
    /**
     * 开盘价
     */
    BigDecimal openPrice;
    /**
     * 最高价
     */
    BigDecimal highestPrice;
    /**
     * 最低价
     */
    BigDecimal lowestPrice;
    /**
     * 收盘价
     */
    BigDecimal closePrice;
    /**
     * 成交量
     */
    Long dealNum;
}
