package com.fox.spider.stock.service;

import com.fox.spider.stock.StockBaseTests;
import com.fox.spider.stock.constant.StockConst;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 股票工具类测试
 *
 * @author lusongsong
 * @date 2020/11/24 17:26
 */
@SpringBootTest
public class StockToolServiceTests extends StockBaseTests {
    /**
     * 股票工具类
     */
    @Autowired
    StockToolService stockToolService;

    /**
     * 获取最新交易日测试
     */
    @Test
    public void lastDealDate() {
        System.out.println(stockToolService.lastDealDate(StockConst.SM_A));
    }

    /**
     * 涨跌幅限制测试
     */
    @Test
    public void limitRate() {
        System.out.println(stockToolService.limitRate(TEST_SH_STOCK, "顶点软件"));
    }
}
