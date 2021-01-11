package com.fox.spider.stock;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;

/**
 * 股票测试基类
 *
 * @author lusongsong
 * @date 2020/11/20 14:16
 */
public class StockBaseTests {
    /**
     * 沪市测试股票（顶点软件）
     */
    public static final StockVo TEST_SH_STOCK = new StockVo("603383",StockConst.SM_SH);
    /**
     * 深市测试股票（同花顺）
     */
    public static final StockVo TEST_SZ_STOCK = new StockVo("000001", StockConst.SM_SZ);
    /**
     * 港股测试股票（腾讯控股）
     */
    public static final StockVo TEST_HK_STOCK =  new StockVo("00700", StockConst.SM_HK);
}
