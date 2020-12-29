package com.fox.spider.stock.entity.po.tencent;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 腾讯实时价格成交量
 *
 * @author lusongsong
 * @date 2020/12/29 17:11
 */
@Data
public class TencentRealtimePriceDealNumPo implements Serializable {
    /**
     * 日期
     */
    String dt;
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
    /**
     * 价格成交量
     */
    List<TencentRealtimePriceDealNumInfoPo> priceDealNumList;
}
