package com.fox.spider.stock.api.tencent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.tencent.TencentRealtimePriceDealNumInfoPo;
import com.fox.spider.stock.entity.po.tencent.TencentRealtimePriceDealNumPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯实时价格成交量接口
 *
 * @author lusongsong
 * @date 2020/12/29 17:10
 */
@Component
public class TencentRealtimePriceDealNumApi extends TencentBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * https://stock.gtimg.cn/data/index.php?appn=price&c=sh603383
     */
    private static final String API_URL = "https://stock.gtimg.cn/data/index.php";
    /**
     * 价格成交量详情分割符
     */
    private static String PRICE_DEAL_NUM_SPLIT_STR = "^";
    /**
     * 价格成交量详情信息分割符
     */
    private static String PRICE_DEAL_NUM_INFO_SPLIT_STR = "~";

    /**
     * 获取实时价格成交量数据
     *
     * @param stockVo
     * @return
     */
    public TencentRealtimePriceDealNumPo priceDealNum(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()) {
            return null;
        }
        try {
            String tencnetStockCode = TencentBaseApi.tencentStockCode(stockVo);
            Map<String, Object> params = new HashMap<>(2);
            params.put("appn", "price");
            params.put("c", tencnetStockCode);
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParams(params)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            TencentRealtimePriceDealNumPo tencentRealtimePriceDealNumPo = this.handleResponse(
                    stockVo, httpResponse.getContent()
            );
            return tencentRealtimePriceDealNumPo;
        } catch (IOException e) {
            logger.error(stockVo.toString(), e);
        }
        return null;
    }

    /**
     * 解析返回数据
     *
     * @param stockVo
     * @param response
     * @return
     */
    private TencentRealtimePriceDealNumPo handleResponse(StockVo stockVo, String response) {
        if (null == stockVo || null == response || response.isEmpty()) {
            return null;
        }
        try {
            int infoStartIndex = response.indexOf("[");
            int infoEndIndex = response.lastIndexOf("]");
            if (-1 == infoStartIndex || -1 == infoEndIndex) {
                return null;
            }
            response = response.substring(infoStartIndex, infoEndIndex + 1);
            JSONArray jsonArray = JSONArray.parseArray(response);
            if (null == jsonArray || jsonArray.isEmpty()) {
                return null;
            }
            TencentRealtimePriceDealNumPo tencentRealtimePriceDealNumPo = new TencentRealtimePriceDealNumPo();
            tencentRealtimePriceDealNumPo.setDt(
                    DateUtil.dateStrFormatChange(
                            jsonArray.getString(0),
                            DateUtil.DATE_FORMAT_2,
                            DateUtil.DATE_FORMAT_1
                    )
            );
            Long dealNum = 0L;
            Long flatDealNum = 0L;
            Long buyDealNum = 0L;
            Long sellDealNum = 0L;
            String priceDealNumStr = jsonArray.getString(3);
            if (null == priceDealNumStr || priceDealNumStr.isEmpty()) {
                return null;
            }
            String[] priceDealNumArr = StringUtils.split(priceDealNumStr, PRICE_DEAL_NUM_SPLIT_STR);
            List<TencentRealtimePriceDealNumInfoPo> tencentRealtimePriceDealNumInfoPoList = new ArrayList<>();
            for (int i = 0; i < priceDealNumArr.length; i++) {
                String singlePriceDealNumStr = priceDealNumArr[i];
                if (null == singlePriceDealNumStr || singlePriceDealNumStr.isEmpty()) {
                    continue;
                }
                TencentRealtimePriceDealNumInfoPo tencentRealtimePriceDealNumInfoPo =
                        handleSinglePriceDealNum(stockVo, singlePriceDealNumStr);
                if (null != tencentRealtimePriceDealNumInfoPo) {
                    if (null != tencentRealtimePriceDealNumInfoPo.getDealNum()) {
                        dealNum += tencentRealtimePriceDealNumInfoPo.getDealNum();
                    }
                    if (null != tencentRealtimePriceDealNumInfoPo.getBuyDealNum()) {
                        buyDealNum += tencentRealtimePriceDealNumInfoPo.getBuyDealNum();
                    }
                    if (null != tencentRealtimePriceDealNumInfoPo.getSellDealNum()) {
                        sellDealNum += tencentRealtimePriceDealNumInfoPo.getSellDealNum();
                    }
                    if (null != tencentRealtimePriceDealNumInfoPo.getFlatDealNum()) {
                        flatDealNum += tencentRealtimePriceDealNumInfoPo.getFlatDealNum();
                    }
                    tencentRealtimePriceDealNumInfoPoList.add(tencentRealtimePriceDealNumInfoPo);
                }
            }
            tencentRealtimePriceDealNumPo.setDealNum(dealNum);
            tencentRealtimePriceDealNumPo.setBuyDealNum(buyDealNum);
            tencentRealtimePriceDealNumPo.setSellDealNum(sellDealNum);
            tencentRealtimePriceDealNumPo.setFlatDealNum(flatDealNum);
            tencentRealtimePriceDealNumPo.setPriceDealNumList(tencentRealtimePriceDealNumInfoPoList);
            return tencentRealtimePriceDealNumPo;
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }

    /**
     * 处理单条价格成交量详情
     *
     * @param stockVo
     * @param singlePriceDealNumStr
     * @return
     */
    private TencentRealtimePriceDealNumInfoPo handleSinglePriceDealNum(StockVo stockVo, String singlePriceDealNumStr) {
        if (null == singlePriceDealNumStr || singlePriceDealNumStr.isEmpty()) {
            return null;
        }
        String[] priceDealNumInfoArr = StringUtils.split(singlePriceDealNumStr, PRICE_DEAL_NUM_INFO_SPLIT_STR);
        TencentRealtimePriceDealNumInfoPo tencentRealtimePriceDealNumInfoPo = new TencentRealtimePriceDealNumInfoPo();
        for (int i = 0; i < priceDealNumInfoArr.length; i++) {
            if (0 == i) {
                tencentRealtimePriceDealNumInfoPo.setPrice(BigDecimalUtil.initPrice(priceDealNumInfoArr[i]));
            }
            if (1 == i) {
                tencentRealtimePriceDealNumInfoPo.setBuyDealNum(handleDealNum(stockVo, priceDealNumInfoArr[i]));
            }
            if (2 == i) {
                tencentRealtimePriceDealNumInfoPo.setDealNum(handleDealNum(stockVo, priceDealNumInfoArr[i]));
            }
            if (3 == i) {
                tencentRealtimePriceDealNumInfoPo.setSellDealNum(handleDealNum(stockVo, priceDealNumInfoArr[i]));
            }
            if (4 == i) {
                tencentRealtimePriceDealNumInfoPo.setFlatDealNum(handleDealNum(stockVo, priceDealNumInfoArr[i]));
            }
        }
        return tencentRealtimePriceDealNumInfoPo;
    }
}
