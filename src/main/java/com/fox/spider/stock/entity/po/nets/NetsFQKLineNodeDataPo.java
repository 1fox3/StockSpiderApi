package com.fox.spider.stock.entity.po.nets;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 网易复权类型k线数据点
 *
 * @author lusongsong
 * @date 2020/11/6 14:42
 */
@Data
public class NetsFQKLineNodeDataPo implements Serializable {
    /**
     * 日期
     */
    String dt;
    /**
     * 开盘价
     */
    BigDecimal openPrice;
    /**
     * 收盘价
     */
    BigDecimal closePrice;
    /**
     * 最高价
     */
    BigDecimal highestPrice;
    /**
     * 最低价
     */
    BigDecimal lowestPrice;
    /**
     * 成交数量
     */
    Long dealNum;
    /**
     * 增幅
     */
    BigDecimal uptickRate;
}
