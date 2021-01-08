package com.fox.spider.stock.api;

import com.fox.spider.stock.StockBaseTests;
import com.fox.spider.stock.api.ifeng.*;
import com.fox.spider.stock.constant.StockConst;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * 凤凰网股票接口测试类
 *
 * @author lusongsong
 * @date 2021/1/4 16:17
 */
@SpringBootTest
public class IFengApiTests extends StockBaseTests {
    /**
     * 凤凰网接口
     */
    @Autowired
    IFengRealtimeDealInfoApi iFengRealtimeDealInfoApi;
    @Autowired
    IFengFiveDayMinuteKLineApi iFengFiveDayMinuteKLineApi;
    @Autowired
    IFengFiveDayMinuteScopeKLineApi iFengFiveDayMinuteScopeKLineApi;
    @Autowired
    IFengKLineApi iFengKLineApi;
    @Autowired
    IFengRealtimeBigDealApi iFengRealtimeBigDealApi;
    @Autowired
    IFengRealtimeDealDetailApi iFengRealtimeDealDetailApi;
    @Autowired
    IFengRealtimePriceDealNumApi iFengRealtimePriceDealNumApi;

    /**
     * 交易日成交信息测试
     */
    @Test
    void batchRealtimeDealInfoTest() {
        System.out.println(
                iFengRealtimeDealInfoApi.batchRealtimeDealInfo(
                        Arrays.asList(TEST_HK_STOCK)
                )
        );
    }

    /**
     * 5日分钟线图测试
     */
    @Test
    void fiveDayKLineTest() {
        System.out.println(
                iFengFiveDayMinuteKLineApi.fiveDayKLine(TEST_SH_STOCK)
        );
    }

    /**
     * 不同分钟粒度线图测试
     */
    @Test
    void minuteScopeKLineTest() {
        System.out.println(
                iFengFiveDayMinuteScopeKLineApi.minuteScopeKLine(TEST_SH_STOCK, 15)
        );
    }

    /**
     * 不同分钟粒度线图测试
     */
    @Test
    void kLineTest() {
        System.out.println(
                iFengKLineApi.kLine(TEST_SH_STOCK, StockConst.DT_MONTH)
        );
    }

    /**
     * 大单交易测试
     */
    @Test
    void bigDealTest() {
        System.out.println(
                iFengRealtimeBigDealApi.bigDeal(TEST_SH_STOCK)
        );
    }

    /**
     * 交易明细测试
     */
    @Test
    void dealDetailTest() {
        System.out.println(
                iFengRealtimeDealDetailApi.dealDetail(TEST_SH_STOCK)
        );
    }

    /**
     * 价格成交量测试
     */
    @Test
    void priceDealNumTest() {
        System.out.println(
                iFengRealtimePriceDealNumApi.priceDealNum(TEST_SH_STOCK)
        );
    }
}
