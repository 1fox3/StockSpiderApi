package com.fox.spider.stock.api.tencent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.tencent.TencentRelateStockPo;
import com.fox.spider.stock.entity.vo.StockVo;
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
 * 滕讯相关股票列表接口
 *
 * @author lusongsong
 * @date 2020/12/29 17:55
 */
@Component
public class TencentRelateStockApi extends TencentBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * https://proxy.finance.qq.com/ifzqgtimg/stock/relate/data/relate?code=sz000002
     */
    private static final String API_URL = "https://proxy.finance.qq.com/ifzqgtimg/stock/relate/data/relate";

    /**
     * 获取相关股票列表
     *
     * @param stockVo
     * @return
     */
    public List<TencentRelateStockPo> relateStock(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()) {
            return null;
        }
        try {
            String tencnetStockCode = TencentBaseApi.tencentStockCode(stockVo);
            Map<String, Object> params = new HashMap<>(1);
            params.put("code", tencnetStockCode);
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParams(params)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            List<TencentRelateStockPo> tencentRelateStockPoList = handleResponse(httpResponse.getContent());
            return tencentRelateStockPoList;
        } catch (IOException e) {
            logger.error(stockVo.toString(), e);
        }
        return null;
    }

    /**
     * 解析返回数据
     *
     * @param response
     * @return
     */
    private List<TencentRelateStockPo> handleResponse(String response) {
        if (null == response || response.isEmpty()) {
            return null;
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (null == jsonObject || !jsonObject.containsKey(RESPONSE_KEY_DATA)) {
                return null;
            }
            JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_KEY_DATA);
            if (null == jsonArray || jsonArray.isEmpty()) {
                return null;
            }
            List<TencentRelateStockPo> tencentRelateStockPoList = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject relateJSONObject = jsonArray.getJSONObject(i);
                if (!relateJSONObject.containsKey(RESPONSE_KEY_CODE) || !relateJSONObject.containsKey(RESPONSE_KEY_NAME)) {
                    continue;
                }
                String tencentStockCode = relateJSONObject.getString(RESPONSE_KEY_CODE);
                StockVo stockVo = TencentBaseApi.tencnetStockCodeToStockVo(tencentStockCode);
                if (null != stockVo) {
                    TencentRelateStockPo tencentRelateStockPo = new TencentRelateStockPo();
                    tencentRelateStockPo.setStockMarket(stockVo.getStockMarket());
                    tencentRelateStockPo.setStockCode(stockVo.getStockCode());
                    tencentRelateStockPo.setStockName(relateJSONObject.getString(RESPONSE_KEY_NAME));
                    tencentRelateStockPoList.add(tencentRelateStockPo);
                }
            }
            return tencentRelateStockPoList;
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }
}
