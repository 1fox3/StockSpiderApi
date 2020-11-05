package com.fox.spider.stock;

import com.fox.spider.stock.api.sina.*;
import com.fox.spider.stock.entity.vo.StockVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockApplicationTests {

    @Autowired
    SinaMinuteKLineData sinaMinuteKLineData;
    @Autowired
    SinaPriceDealNumRatio sinaPriceDealNumRatio;
    @Autowired
    SinaRealtimePriceDealNumRatio sinaRealtimePriceDealNumRatio;
    @Autowired
    SinaRealtimeDealInfo sinaRealtimeDealInfo;
    @Autowired
    SinaFQPriceLine sinaFQPriceLine;

    /**
     * 新浪接口测试
     */
    private void sinaTest() {
        StockVo stockVo = new StockVo("002095", 2);
//        StockVo stockVo = new StockVo("00700", 3);
        String startDate = "2020-11-04";
        String endDate = "2020-11-04";
        System.out.println(sinaMinuteKLineData.kLineDataList(stockVo, 5, 120));
        System.out.println(sinaPriceDealNumRatio.priceDealNumRatio(stockVo, startDate, endDate));
        System.out.println(sinaRealtimePriceDealNumRatio.priceDealNumRatio(stockVo));
        System.out.println(sinaRealtimeDealInfo.realtimeDealInfo(stockVo));
        System.out.println(sinaFQPriceLine.fqPriceLine(stockVo, 1));
    }

    @Test
    void contextLoads() {
    }

}
