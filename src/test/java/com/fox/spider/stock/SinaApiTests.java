package com.fox.spider.stock;

import com.fox.spider.stock.api.sina.*;
import com.fox.spider.stock.constant.StockConst;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 新浪股票接口测试
 *
 * @author lusongsong
 * @date 2020/11/6 14:05
 */
@SpringBootTest
class SinaApiTests extends StockApiBaseTests {
    /**
     * 新浪接口
     */
    @Autowired
    SinaMinuteKLineDataApi sinaMinuteKLineDataApi;
    @Autowired
    SinaPriceDealNumRatioApi sinaPriceDealNumRatioApi;
    @Autowired
    SinaRealtimePriceDealNumRatioApi sinaRealtimePriceDealNumRatioApi;
    @Autowired
    SinaRealtimeDealInfoApi sinaRealtimeDealInfoApi;
    @Autowired
    SinaFQPriceLineApi sinaFQPriceLineApi;

    /**
     * 分钟粒度成交信息测试
     */
    @Test
    void minuteKLineDataTest() {
        Integer scale = 5;
        Integer dataLen = 120;
        System.out.println(sinaMinuteKLineDataApi.kLineDataList(TEST_SH_STOCK, scale, dataLen));
    }

    /**
     * 股票成交价格占比测试
     */
    @Test
    void priceDealNumRatioTest() {
        String startDate = "2020-11-04";
        String endDate = "2020-11-04";
        System.out.println(sinaPriceDealNumRatioApi.priceDealNumRatio(TEST_SH_STOCK, startDate, endDate));
    }

    /**
     * 实时成交价格占比测试
     */
    @Test
    void realtimePriceDealNumRatioTest() {
        System.out.println(sinaRealtimePriceDealNumRatioApi.priceDealNumRatio(TEST_SH_STOCK));
    }

    /**
     * 实时交易数据测试
     */
    @Test
    void realtimeDealInfoTest() {
        System.out.println(sinaRealtimeDealInfoApi.realtimeDealInfo(TEST_SH_STOCK));
    }

    /**
     * 复权价格信息
     */
    @Test
    void fqPriceLineTest() {
        System.out.println(sinaFQPriceLineApi.fqPriceLine(TEST_SH_STOCK, StockConst.SFQ_BEFORE));
    }
}
