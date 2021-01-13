package com.fox.spider.stock.api;

import com.fox.spider.stock.StockBaseTests;
import com.fox.spider.stock.api.sz.SZKLineApi;
import com.fox.spider.stock.api.sz.SZRealtimeDealApi;
import com.fox.spider.stock.api.sz.SZStockInfoApi;
import com.fox.spider.stock.constant.StockConst;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 深市交易所接口测试类
 *
 * @author lusongsong
 * @date 2020/11/20 14:38
 */
@SpringBootTest
public class SZApiTests extends StockBaseTests {
    /**
     * 深市测试股票编码
     */
    private static final String TEST_SZ_STOCK_CODE = TEST_SZ_STOCK.getStockCode();
    /**
     * 深市接口
     */
    @Autowired
    SZStockInfoApi szStockInfoApi;
    @Autowired
    SZRealtimeDealApi szRealtimeDealApi;
    @Autowired
    SZKLineApi szkLineApi;

    /**
     * 按天成交信息测试
     */
    @Test
    void stockInfoTest() {
        System.out.println(szStockInfoApi.stockInfo(TEST_SZ_STOCK_CODE));
    }

    /**
     * 深证股票最新交易日交易数据测试
     */
    @Test
    void realtimeDealTest() {
        System.out.println(szRealtimeDealApi.realtimeDeal(TEST_SZ_STOCK));
    }

    /**
     * 深证股票k线数据测试
     */
    @Test
    void kLineTest() {
        System.out.println(szkLineApi.kline(TEST_SZ_STOCK, StockConst.DT_DAY));
    }
}
