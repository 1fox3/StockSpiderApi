package com.fox.spider.stock.api.tencent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.tencent.TencentDayMinKLinePo;
import com.fox.spider.stock.entity.po.tencent.TencentFiveDayMinuteKLinePo;
import com.fox.spider.stock.entity.po.tencent.TencentRealtimeDealInfoPo;
import com.fox.spider.stock.entity.po.tencent.TencentRealtimeMinuteNodeDataPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯股票5日分钟线图
 *
 * @author lusongsong
 * @date 2020/12/28 15:41
 */
@Component
public class TencentFiveDayMinuteKLineApi extends TencentKLineBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * https://web.ifzq.gtimg.cn/appstock/app/day/query?_var=fdays_data&code=sz000002
     */
    private static final String API_URL = "https://web.ifzq.gtimg.cn/appstock/app/day/query";
    /**
     * _var参数值
     */
    private static String PARAM_VAR = "fdays_data";

    /**
     * 获取股票5日分钟线图
     *
     * @param stockVo
     * @return
     */
    public TencentFiveDayMinuteKLinePo fiveDayKLine(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()) {
            return null;
        }
        try {
            String tencnetStockCode = TencentBaseApi.tencentStockCode(stockVo);
            Map<String, String> params = new HashMap<>(2);
            params.put("_var", PARAM_VAR);
            params.put("code", tencnetStockCode);
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParams(params)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            TencentFiveDayMinuteKLinePo tencentFiveDayMinuteKLinePo = this.handleResponse(
                    stockVo, httpResponse.getContent()
            );
            if (null != tencentFiveDayMinuteKLinePo) {
                tencentFiveDayMinuteKLinePo.setStockMarket(stockVo.getStockMarket());
                tencentFiveDayMinuteKLinePo.setStockCode(stockVo.getStockCode());
            }
            return tencentFiveDayMinuteKLinePo;
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
    private TencentFiveDayMinuteKLinePo handleResponse(StockVo stockVo, String response) {
        if (null == stockVo || null == response || response.isEmpty()) {
            return null;
        }
        try {
            String stockCode = TencentBaseApi.tencentStockCode(stockVo);
            JSONObject jsonObject = getResponseJSONObject(stockVo, response);
            JSONObject dealInfoObject = jsonObject.containsKey(RESPONSE_KEY_QT) ?
                    jsonObject.getJSONObject(RESPONSE_KEY_QT) : null;
            TencentFiveDayMinuteKLinePo tencentFiveDayMinuteKLinePo = new TencentFiveDayMinuteKLinePo();
            if (null != dealInfoObject && dealInfoObject.containsKey(stockCode)) {
                JSONArray dealInfoArr = dealInfoObject.getJSONArray(stockCode);
                TencentRealtimeDealInfoPo tencentRealtimeDealInfoPo =
                        TencentRealtimeDealInfoApi.getDealInfo(stockVo, dealInfoArr.toArray(new String[]{}));
                if (null != tencentRealtimeDealInfoPo) {
                    tencentFiveDayMinuteKLinePo.setRealtimeDealInfo(tencentRealtimeDealInfoPo);
                    tencentFiveDayMinuteKLinePo.setStockName(
                            tencentRealtimeDealInfoPo.getStockName()
                    );
                }
            }
            JSONArray fiveDayArr = jsonObject.containsKey(RESPONSE_KEY_DATA) ?
                    jsonObject.getJSONArray(RESPONSE_KEY_DATA) : null;
            if (null != fiveDayArr) {
                List<TencentDayMinKLinePo> tencentDayMinKLinePoList = new ArrayList<>();
                for (int dateIndex = fiveDayArr.size() - 1; dateIndex >= 0; dateIndex--) {
                    JSONObject oneDayObject = fiveDayArr.getJSONObject(dateIndex);
                    if (null == oneDayObject) {
                        continue;
                    }
                    TencentDayMinKLinePo tencentDayMinKLinePo = new TencentDayMinKLinePo();
                    if (oneDayObject.containsKey(RESPONSE_KEY_DATE)) {
                        tencentDayMinKLinePo.setDt(
                                DateUtil.dateStrFormatChange(
                                        oneDayObject.getString(RESPONSE_KEY_DATE),
                                        DateUtil.DATE_FORMAT_2,
                                        DateUtil.DATE_FORMAT_1
                                )
                        );
                    }
                    if (oneDayObject.containsKey(RESPONSE_KEY_PRE_CLOSE_PRICE)) {
                        tencentDayMinKLinePo.setPreClosePrice(
                                BigDecimalUtil.initPrice(oneDayObject.getString(RESPONSE_KEY_PRE_CLOSE_PRICE))
                        );
                    }
                    if (oneDayObject.containsKey(RESPONSE_KEY_DATA)) {
                        JSONArray minKLineArr = oneDayObject.getJSONArray(RESPONSE_KEY_DATA);
                        if (null != minKLineArr && !minKLineArr.isEmpty()) {
                            List<TencentRealtimeMinuteNodeDataPo> tencentRealtimeMinuteNodeDataPoList =
                                    handleDayMinArr(stockVo, minKLineArr);
                            if (null != tencentRealtimeMinuteNodeDataPoList
                                    && !tencentRealtimeMinuteNodeDataPoList.isEmpty()) {
                                tencentDayMinKLinePo.setNodeCount(tencentRealtimeMinuteNodeDataPoList.size());
                                tencentDayMinKLinePo.setKlineData(tencentRealtimeMinuteNodeDataPoList);
                            }
                        }
                    }
                    tencentDayMinKLinePoList.add(tencentDayMinKLinePo);
                }
                tencentFiveDayMinuteKLinePo.setKlineData(tencentDayMinKLinePoList);
            }
            return tencentFiveDayMinuteKLinePo;
        } catch (Exception e) {
            logger.error(response, e);
        }
        return null;
    }
}
