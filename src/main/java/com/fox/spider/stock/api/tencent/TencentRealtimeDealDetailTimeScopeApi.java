package com.fox.spider.stock.api.tencent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.vo.StockVo;
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
 * 腾讯股票交易实时明细时间范围列表
 *
 * @author lusongsong
 * @date 2020/12/29 16:40
 */
@Component
public class TencentRealtimeDealDetailTimeScopeApi extends TencentBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * https://stock.gtimg.cn/data/index.php?appn=detail&action=timeline&c=sh603383
     */
    private static final String API_URL = "https://stock.gtimg.cn/data/index.php";
    /**
     * 时间范围分割符
     */
    private static String SCOPE_SPLIT_STR = "|";
    /**
     * 时间分割符
     */
    private static String TIME_SPLIT_STR = "~";

    /**
     * 获取交易实时明细时间范围列表
     *
     * @param stockVo
     * @return
     */
    public List<List<String>> dealDetailTimeScope(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()) {
            return null;
        }
        try {
            String tencnetStockCode = TencentBaseApi.tencentStockCode(stockVo);
            Map<String, String> params = new HashMap<>(4);
            params.put("appn", "detail");
            params.put("action", "timeline");
            params.put("c", tencnetStockCode);
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParams(params)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            List<List<String>> dealDetailTimeScopeList = this.handleResponse(
                    stockVo, httpResponse.getContent()
            );
            return dealDetailTimeScopeList;
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
    private List<List<String>> handleResponse(StockVo stockVo, String response) {
        if (null == stockVo || null == response || response.isEmpty()) {
            return null;
        }
        try {
            int infoStartIndex = response.indexOf("[");
            if (-1 == infoStartIndex) {
                return null;
            }
            response = response.substring(infoStartIndex);
            JSONArray jsonArray = JSONArray.parseArray(response);
            if (null == jsonArray || jsonArray.isEmpty() || 2 != jsonArray.size()) {
                return null;
            }
            String timeScopeStr = jsonArray.getString(1);
            if (null == timeScopeStr || timeScopeStr.isEmpty()) {
                return null;
            }
            String[] dealDetailArr = StringUtils.split(timeScopeStr, SCOPE_SPLIT_STR);
            List<List<String>> timeScopeList = new ArrayList<>();
            for (int i = 0; i < dealDetailArr.length; i++) {
                String singleTimeScopeStr = dealDetailArr[i];
                if (null == singleTimeScopeStr || singleTimeScopeStr.isEmpty()) {
                    continue;
                }
                List<String> scopeList = handleSingleTimeScope(singleTimeScopeStr);
                if (null != scopeList) {
                    timeScopeList.add(scopeList);
                }
            }
            return timeScopeList;
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }

    /**
     * 处理单条时间范围
     *
     * @param singleTimeScopeStr
     * @return
     */
    private List<String> handleSingleTimeScope(String singleTimeScopeStr) {
        if (null == singleTimeScopeStr || singleTimeScopeStr.isEmpty()) {
            return null;
        }
        String[] timeScopeInfoArr = StringUtils.split(singleTimeScopeStr, TIME_SPLIT_STR);
        if (null == timeScopeInfoArr || 2 != timeScopeInfoArr.length) {
            return null;
        }
        List<String> scopeList = new ArrayList<>(2);
        for (int i = 0; i < timeScopeInfoArr.length; i++) {
            scopeList.add(timeScopeInfoArr[i]);
        }
        return scopeList;
    }
}
