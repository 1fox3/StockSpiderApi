package com.fox.spider.stock.api;

import com.fox.spider.stock.StockBaseTests;
import com.fox.spider.stock.api.hk.HKKLineApi;
import com.fox.spider.stock.api.hk.HKStockInfoApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 香港交易所接口测试
 *
 * @author lusongsong
 * @date 2020/11/20 15:15
 */
@SpringBootTest
public class HKApiTests extends StockBaseTests {
    /**
     * 港股测试股票编码
     */
    private static final String TEST_HK_STOCK_CODE = TEST_HK_STOCK.getStockCode();
    /**
     * 港股接口
     */
    @Autowired
    HKStockInfoApi hkStockInfoApi;
    @Autowired
    HKKLineApi hkkLineApi;


    /**
     * 按天成交信息测试
     */
    @Test
    void stockInfoTest() {
        System.out.println(hkStockInfoApi.stockInfo(
                hkStockInfoApi.apiToken(), TEST_HK_STOCK_CODE
        ));
    }

    /**
     * K线数据测试
     */
    @Test
    void kLineTest() {
        System.out.println(hkkLineApi.kLine(
                TEST_HK_STOCK, HKKLineApi.HK_KLINE_MINUTE, hkStockInfoApi.apiToken()
        ));
    }
}
