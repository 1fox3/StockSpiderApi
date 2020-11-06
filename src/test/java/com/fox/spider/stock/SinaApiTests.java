package com.fox.spider.stock;

import com.fox.spider.stock.api.sina.*;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;
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
class SinaApiTests {
    /**
     * 新浪接口
     */
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
     * 股票实例
     */
    private StockVo stockVo = new StockVo("603383", StockConst.SM_SH);//顶点软件
//    private StockVo stockVo = new StockVo("300033", StockConst.SM_SZ);//同花顺
//    private StockVo stockVo = new StockVo("00700", StockConst.SM_HK);//腾讯

    /**
     * 分钟粒度成交信息测试
     */
    @Test
    void minuteKLineDataTest() {
        Integer scale = 5;
        Integer dataLen = 120;
        System.out.println(sinaMinuteKLineData.kLineDataList(stockVo, scale, dataLen));
    }

    /**
     * 股票成交价格占比测试
     */
    @Test
    void priceDealNumRatioTest() {
        String startDate = "2020-11-04";
        String endDate = "2020-11-04";
        System.out.println(sinaPriceDealNumRatio.priceDealNumRatio(stockVo, startDate, endDate));
    }

    /**
     * 实时成交价格占比测试
     */
    @Test
    void realtimePriceDealNumRatioTest() {
        System.out.println(sinaRealtimePriceDealNumRatio.priceDealNumRatio(stockVo));
    }

    /**
     * 实时交易数据测试
     */
    @Test
    void realtimeDealInfoTest() {
        System.out.println(sinaRealtimeDealInfo.realtimeDealInfo(stockVo));
    }

    /**
     * 复权价格信息
     */
    @Test
    void fqPriceLineTest() {
        System.out.println(sinaFQPriceLine.fqPriceLine(stockVo, StockConst.SFQ_BEFORE));
    }
}
