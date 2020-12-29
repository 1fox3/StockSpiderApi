package com.fox.spider.stock.api.tencent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.tencent.TencentRelateBlockPo;
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
 * 滕讯股票所属板块接口
 *
 * @author lusongsong
 * @date 2020/12/29 18:15
 */
@Component
public class TencentRelateBlockApi extends TencentBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * https://proxy.finance.qq.com/ifzqgtimg/stock/relate/data/plate?code=sz000002
     */
    private static final String API_URL = "https://proxy.finance.qq.com/ifzqgtimg/stock/relate/data/plate";

    /**
     * 获取股票所属板块
     *
     * @param stockVo
     * @return
     */
    public List<TencentRelateBlockPo> relateBlock(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()) {
            return null;
        }
        try {
            String tencnetStockCode = TencentBaseApi.tencentStockCode(stockVo);
            Map<String, String> params = new HashMap<>(1);
            params.put("code", tencnetStockCode);
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParams(params)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            List<TencentRelateBlockPo> tencentRelateBlockPoList = handleResponse(httpResponse.getContent());
            return tencentRelateBlockPoList;
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
    private List<TencentRelateBlockPo> handleResponse(String response) {
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
            List<TencentRelateBlockPo> tencentRelateBlockPoList = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject relateJSONObject = jsonArray.getJSONObject(i);
                if (!relateJSONObject.containsKey(RESPONSE_KEY_CODE) || !relateJSONObject.containsKey(RESPONSE_KEY_NAME)) {
                    continue;
                }
                TencentRelateBlockPo tencentRelateBlockPo = new TencentRelateBlockPo();
                tencentRelateBlockPo.setCode(relateJSONObject.getString(RESPONSE_KEY_CODE));
                tencentRelateBlockPo.setName(relateJSONObject.getString(RESPONSE_KEY_NAME));
                tencentRelateBlockPoList.add(tencentRelateBlockPo);
            }
            return tencentRelateBlockPoList;
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }
}
