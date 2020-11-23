package com.fox.spider.stock.entity.po.sz;

import com.fox.spider.stock.constant.StockConst;
import lombok.Data;

import java.io.Serializable;

/**
 * 深市股票信息
 *
 * @author lusongsong
 * @date 2020/11/20 14:31
 */
@Data
public class SZStockInfoPo implements Serializable {
    /**
     * 股票所属交易所
     */
    Integer stockMarket = StockConst.SM_SZ;
    /**
     * 股票代码
     */
    String stockCode = "";
    /**
     * 股票名称
     */
    String stockName = "";
    /**
     * 股票全称
     */
    String stockFullName = "";
    /**
     * 股票英文全称
     */
    String stockFullNameEn = "";
    /**
     * 股票公司注册地址
     */
    String stockRegisterAddress = "";
    /**
     * 股票公司官方网址
     */
    String stockWebsite = "";
    /**
     * 股票上市日期
     */
    String stockOnDate = "";
    /**
     * 股票总股本(万)
     */
    Double stockTotalEquity = 0.0;
    /**
     * 股票流通股本(万)
     */
    Double stockCircEquity = 0.0;
    /**
     * 股票公司所在地理位置区域
     */
    String stockArea = "";
    /**
     * 股票公司所在省份
     */
    String stockProvince = "";
    /**
     * 股票公司所在城市
     */
    String stockCity = "";
    /**
     * 股票所属行业
     */
    String stockIndustry = "";
}
