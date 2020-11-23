package com.fox.spider.stock.entity.vo;

import com.fox.spider.stock.constant.StockConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 股票所属分类
 *
 * @author lusongsong
 * @date 2020/11/12 15:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockCategoryVo implements Serializable {
    /**
     * 股市
     */
    Integer stockMarket = StockConst.SM_UNKNOWN;
    /**
     * 类型
     */
    Integer stockType = StockConst.ST_UNKNOWN;
    /**
     * 分类
     */
    Integer stockKind = StockConst.SK_UNKNOWN;
}
