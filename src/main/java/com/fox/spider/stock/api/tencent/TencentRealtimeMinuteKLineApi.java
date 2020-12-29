package com.fox.spider.stock.api.tencent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.tencent.TencentRealtimeMinuteKLinePo;
import com.fox.spider.stock.entity.po.tencent.TencentRealtimeMinuteNodeDataPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯股票实时分钟线图
 *
 * @author lusongsong
 * @date 2020/12/25 14:55
 */
@Component
public class TencentRealtimeMinuteKLineApi extends TencentKLineBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * https://web.ifzq.gtimg.cn/appstock/app/minute/query?_var=min_data&code=sz000002
     */
    private static final String API_URL = "https://web.ifzq.gtimg.cn/appstock/app/minute/query";
    /**
     * _var参数值
     */
    private static String PARAM_VAR = "min_data";

    /**
     * 获取实时交易分钟线图数据
     *
     * @param stockVo
     * @return
     */
    public TencentRealtimeMinuteKLinePo realtimeMinuteKLine(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()) {
            return null;
        }
        try {
            String tencnetStockCode = TencentBaseApi.tencentStockCode(stockVo);
            if (null == tencnetStockCode && tencnetStockCode.isEmpty()) {
                return null;
            }
            Map<String, String> params = new HashMap<>(2);
            params.put("_var", PARAM_VAR);
            params.put("code", tencnetStockCode);
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParams(params)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            TencentRealtimeMinuteKLinePo tencentRealtimeMinuteKLinePo = this.handleResponse(
                    stockVo, httpResponse.getContent()
            );
            if (null != tencentRealtimeMinuteKLinePo) {
                tencentRealtimeMinuteKLinePo.setStockMarket(stockVo.getStockMarket());
                tencentRealtimeMinuteKLinePo.setStockCode(stockVo.getStockCode());
            }
            return tencentRealtimeMinuteKLinePo;
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
    private TencentRealtimeMinuteKLinePo handleResponse(StockVo stockVo, String response) {
        if (null == stockVo || null == response || response.isEmpty()) {
            return null;
        }
        try {
            String stockCode = TencentBaseApi.tencentStockCode(stockVo);
            JSONObject jsonObject = getResponseJSONObject(stockVo, response);
            JSONObject dealInfoObject = jsonObject.containsKey(RESPONSE_KEY_QT) ?
                    jsonObject.getJSONObject(RESPONSE_KEY_QT) : null;
            TencentRealtimeMinuteKLinePo tencentRealtimeMinuteKLinePo = new TencentRealtimeMinuteKLinePo();
            if (null != dealInfoObject && dealInfoObject.containsKey(stockCode)) {
                JSONArray dealInfoArr = dealInfoObject.getJSONArray(stockCode);
                if (null != dealInfoArr) {
                    int len = dealInfoArr.size();
                    if (1 < len) {
                        tencentRealtimeMinuteKLinePo.setStockName(dealInfoArr.getString(1));
                    }
                    if (4 < len) {
                        tencentRealtimeMinuteKLinePo.setPreClosePrice(
                                BigDecimalUtil.initPrice(dealInfoArr.getString(4))
                        );
                    }
                    if (6 < len) {
                        tencentRealtimeMinuteKLinePo.setDealNum(
                                handleDealNum(stockVo, dealInfoArr.getString(6))
                        );
                    }
                }
            }
            JSONObject minKLineObject = jsonObject.containsKey(RESPONSE_KEY_DATA) ?
                    jsonObject.getJSONObject(RESPONSE_KEY_DATA) : null;
            if (null != minKLineObject) {
                if (minKLineObject.containsKey(RESPONSE_KEY_DATE)) {
                    tencentRealtimeMinuteKLinePo.setDt(
                            DateUtil.dateStrFormatChange(
                                    minKLineObject.getString(RESPONSE_KEY_DATE),
                                    DateUtil.DATE_FORMAT_2,
                                    DateUtil.DATE_FORMAT_1
                            )
                    );
                }
                if (minKLineObject.containsKey(RESPONSE_KEY_DATA)) {
                    JSONArray minKLineArr = minKLineObject.getJSONArray(RESPONSE_KEY_DATA);
                    if (null != minKLineArr && !minKLineArr.isEmpty()) {
                        List<TencentRealtimeMinuteNodeDataPo> tencentRealtimeMinuteNodeDataPoList =
                                handleDayMinArr(stockVo, minKLineArr);
                        if (null != tencentRealtimeMinuteNodeDataPoList
                                && !tencentRealtimeMinuteNodeDataPoList.isEmpty()) {
                            tencentRealtimeMinuteKLinePo.setNodeCount(tencentRealtimeMinuteNodeDataPoList.size());
                            tencentRealtimeMinuteKLinePo.setKlineData(tencentRealtimeMinuteNodeDataPoList);
                        }
                    }
                }
            }
            return tencentRealtimeMinuteKLinePo;
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }
}
