package com.fox.spider.stock.api.sina;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealDetailTimeScopeDataPo;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealDetailTimeScopePo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 新浪股票交易明细时间范围接口
 *
 * @author lusongsong
 * @date 2021/1/8 16:16
 */
@Component
public class SinaRealtimeDealDetailTimeScopeApi extends SinaBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * A股接口地址
     * https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_Transactions.getAllPageTime?symbol=sh603383
     */
    private static final String API_URL =
            "https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_Transactions.getAllPageTime";
    /**
     * 返回数据日期key
     */
    private static final String RESPONSE_KEY_DETAIL_DATE = "detailDate";
    /**
     * 返回数据页码key
     */
    private static final String RESPONSE_KEY_DETAIL_PAGES = "detailPages";
    /**
     * 返回数据页码key
     */
    private static final String RESPONSE_KEY_DETAIL_PAGE = "page";
    /**
     * 返回数据开始时间key
     */
    private static final String RESPONSE_KEY_DETAIL_BEGIN_TS = "begin_ts";
    /**
     * 返回数据结束时间key
     */
    private static final String RESPONSE_KEY_DETAIL_END_TS = "end_ts";

    /**
     * 获取交易明细时间范围
     *
     * @param stockVo
     * @return
     */
    public SinaRealtimeDealDetailTimeScopePo dealDetailTimeScope(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()) {
            return null;
        }
        try {
            String sinaStockCode = sinaStockCode(stockVo);
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParam("symbol", sinaStockCode)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            SinaRealtimeDealDetailTimeScopePo sinaRealtimeDealDetailTimeScopePo =
                    handleResponse(httpResponse.getContent());
            return sinaRealtimeDealDetailTimeScopePo;
        } catch (IOException e) {
            logger.error(stockVo.toString(), e);
        }
        return null;
    }

    /**
     * 处理返回信息
     *
     * @param response
     * @return
     */
    private SinaRealtimeDealDetailTimeScopePo handleResponse(String response) {
        if (!(response instanceof String) || response.isEmpty()) {
            return null;
        }
        try{
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (null != jsonObject) {
                SinaRealtimeDealDetailTimeScopePo sinaRealtimeDealDetailTimeScopePo =
                        new SinaRealtimeDealDetailTimeScopePo();
                if (jsonObject.containsKey(RESPONSE_KEY_DETAIL_DATE)) {
                    sinaRealtimeDealDetailTimeScopePo.setDt(jsonObject.getString(RESPONSE_KEY_DETAIL_DATE));
                }
                if (jsonObject.containsKey(RESPONSE_KEY_DETAIL_PAGES)) {
                    JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_KEY_DETAIL_PAGES);
                    List<SinaRealtimeDealDetailTimeScopeDataPo> sinaRealtimeDealDetailTimeScopeDataPoList =
                            new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject pageJO = jsonArray.getJSONObject(i);
                        if (null != pageJO) {
                            SinaRealtimeDealDetailTimeScopeDataPo sinaRealtimeDealDetailTimeScopeDataPo =
                                    new SinaRealtimeDealDetailTimeScopeDataPo();
                            if (pageJO.containsKey(RESPONSE_KEY_DETAIL_PAGE)) {
                                sinaRealtimeDealDetailTimeScopeDataPo.setPageNum(
                                        pageJO.getInteger(RESPONSE_KEY_DETAIL_PAGE)
                                );
                            }
                            if (pageJO.containsKey(RESPONSE_KEY_DETAIL_BEGIN_TS)) {
                                sinaRealtimeDealDetailTimeScopeDataPo.setStartTime(
                                        pageJO.getString(RESPONSE_KEY_DETAIL_BEGIN_TS)
                                );
                            }
                            if (pageJO.containsKey(RESPONSE_KEY_DETAIL_END_TS)) {
                                sinaRealtimeDealDetailTimeScopeDataPo.setEndTime(
                                        pageJO.getString(RESPONSE_KEY_DETAIL_END_TS)
                                );
                            }
                            sinaRealtimeDealDetailTimeScopeDataPoList.add(sinaRealtimeDealDetailTimeScopeDataPo);
                        }
                    }
                    if (!sinaRealtimeDealDetailTimeScopeDataPoList.isEmpty()) {
                        sinaRealtimeDealDetailTimeScopePo.setTimeScopeList(sinaRealtimeDealDetailTimeScopeDataPoList);
                    }
                }
                return sinaRealtimeDealDetailTimeScopePo;
            }
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }
}
