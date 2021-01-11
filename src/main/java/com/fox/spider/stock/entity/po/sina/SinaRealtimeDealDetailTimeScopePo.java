package com.fox.spider.stock.entity.po.sina;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 新浪网股票最新交易日交易明细时间范围
 *
 * @author lusongsong
 * @date 2021/1/8 16:21
 */
@Data
public class SinaRealtimeDealDetailTimeScopePo implements Serializable {
    /**
     * 日期
     */
    String dt;
    /**
     * 时间范围列表
     */
    List<SinaRealtimeDealDetailTimeScopeDataPo> timeScopeList;
}
