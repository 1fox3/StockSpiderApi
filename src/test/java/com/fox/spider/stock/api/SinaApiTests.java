package com.fox.spider.stock.api;

import com.fox.spider.stock.StockBaseTests;
import com.fox.spider.stock.api.sina.*;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.sina.SinaBigDealFilterVo;
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
class SinaApiTests extends StockBaseTests {
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
    @Autowired
    SinaRealtimeDealDetailTimeScopeApi sinaRealtimeDealDetailTimeScopeApi;
    @Autowired
    SinaRealtimeDealDetailApi sinaRealtimeDealDetailApi;
    @Autowired
    SinaBigDealApi sinaBigDealApi;

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
     * 复权价格信息测试
     */
    @Test
    void fqPriceLineTest() {
        System.out.println(sinaFQPriceLineApi.fqPriceLine(TEST_SH_STOCK, StockConst.SFQ_BEFORE));
    }

    /**
     * 交易明细时间范围测试
     */
    @Test
    void dealDetailTimeScopeTest() {
        System.out.println(sinaRealtimeDealDetailTimeScopeApi.dealDetailTimeScope(TEST_SH_STOCK));
    }

    /**
     * 交易明细测试
     */
    @Test
    void dealDetailTest() {
        System.out.println(sinaRealtimeDealDetailApi.dealDetail(TEST_SH_STOCK, 1));
    }

    /**
     * 大单交易总数测试
     */
    @Test
    void bigDealListCountTest(){
        SinaBigDealFilterVo sinaBigDealFilterVo = new SinaBigDealFilterVo();
        sinaBigDealFilterVo.setDealMoney(500000);
        System.out.println(
                sinaBigDealApi.bigDealListCount(TEST_SH_STOCK, sinaBigDealFilterVo, null)
        );
    }

    /**
     * 大单交易汇总数据测试
     */
    @Test
    void bigDealSumTest(){
        SinaBigDealFilterVo sinaBigDealFilterVo = new SinaBigDealFilterVo();
        sinaBigDealFilterVo.setDealMoney(500000);
        System.out.println(
                sinaBigDealApi.bigDealSum(TEST_SH_STOCK, sinaBigDealFilterVo, null)
        );
    }

    /**
     * 大单交易汇总数据测试
     */
    @Test
    void bigDealListTest(){
        SinaBigDealFilterVo sinaBigDealFilterVo = new SinaBigDealFilterVo();
        sinaBigDealFilterVo.setDealMoney(500000);
        System.out.println(
                sinaBigDealApi.bigDealList(TEST_SH_STOCK, sinaBigDealFilterVo, null,null, null)
        );
    }
}
