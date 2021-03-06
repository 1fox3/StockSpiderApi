package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 腾讯实时价格成交量信息
 *
 * @author lusongsong
 * @date 2020/12/29 17:23
 */
@Data
public class TencentRealtimePriceDealNumInfoPo implements Serializable {
    /**
     * 价格
     */
    BigDecimal price;
    /**
     * 成交量
     */
    Long dealNum;
    /**
     * 买入量
     */
    Long buyDealNum;
    /**
     * 卖出量
     */
    Long sellDealNum;
    /**
     * 中性量
     */
    Long flatDealNum;

}
