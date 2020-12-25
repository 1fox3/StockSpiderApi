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
import java.math.BigDecimal;
import java.util.*;

/**
 * 腾讯股票实时分钟线图
 *
 * @author lusongsong
 * @date 2020/12/25 14:55
 */
@Component
public class TencentRealtimeMinuteKLineApi extends TencentBaseApi {
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
     * 返回数据key
     */
    private static String RESPONSE_KEY_DATA = "data";
    /**
     * 返回数据交易信息key
     */
    private static String RESPONSE_KEY_QT = "qt";
    /**
     * 返回数据日期key
     */
    private static String RESPONSE_KEY_DATE = "date";
    /**
     * 分钟交易信息分割符
     */
    private static String MIN_DEAL_INFO_SPLIT_STR = " ";

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
        int infoStartIndex = response.indexOf("{");
        if (-1 == infoStartIndex) {
            return null;
        }
        String stockCode = TencentBaseApi.tencentStockCode(stockVo);
        try {
            response = response.substring(infoStartIndex);
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (null == jsonObject || !jsonObject.containsKey(RESPONSE_KEY_DATA)) {
                return null;
            }
            jsonObject = jsonObject.getJSONObject(RESPONSE_KEY_DATA);
            if (null == jsonObject || !jsonObject.containsKey(stockCode)) {
                return null;
            }
            jsonObject = jsonObject.getJSONObject(stockCode);
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

                        List<TencentRealtimeMinuteNodeDataPo> tencentRealtimeMinuteNodeDataPoList = new ArrayList<>();
                        Long totalDealNum = 0L;
                        Long currentTotalDealNum = 0L;
                        for (int i = 0; i < minKLineArr.size(); i++) {
                            String minDealInfoStr = minKLineArr.getString(i);
                            if (null == minDealInfoStr || minDealInfoStr.isEmpty()
                                    || minDealInfoStr.contains(MIN_DEAL_INFO_SPLIT_STR)) {
                                String[] minDealInfoArr = minDealInfoStr.split(MIN_DEAL_INFO_SPLIT_STR);
                                if (3 != minDealInfoArr.length) {
                                    continue;
                                }
                                TencentRealtimeMinuteNodeDataPo tencentRealtimeMinuteNodeDataPo =
                                        new TencentRealtimeMinuteNodeDataPo();
                                tencentRealtimeMinuteNodeDataPo.setTime(
                                        DateUtil.dateStrFormatChange(
                                                minDealInfoArr[0],
                                                DateUtil.TIME_FORMAT_5,
                                                DateUtil.TIME_FORMAT_6
                                        )
                                );
                                tencentRealtimeMinuteNodeDataPo.setPrice(BigDecimalUtil.initPrice(minDealInfoArr[1]));
                                currentTotalDealNum = new BigDecimal(minDealInfoArr[2]).longValue();
                                tencentRealtimeMinuteNodeDataPo.setDealNum(
                                        handleDealNum(stockVo, String.valueOf(currentTotalDealNum - totalDealNum))
                                );
                                totalDealNum = currentTotalDealNum;
                                tencentRealtimeMinuteNodeDataPoList.add(tencentRealtimeMinuteNodeDataPo);
                            }
                        }
                        tencentRealtimeMinuteKLinePo.setNodeCount(tencentRealtimeMinuteNodeDataPoList.size());
                        tencentRealtimeMinuteKLinePo.setKlineData(tencentRealtimeMinuteNodeDataPoList);
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
