package com.fox.spider.stock.api.tencent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.tencent.TencentRealtimeDealDetailPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯股票交易实时明细
 *
 * @author lusongsong
 * @date 2020/12/29 15:35
 */
@Component
public class TencentRealtimeDealDetailApi extends TencentBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * https://stock.gtimg.cn/data/index.php?appn=detail&action=data&c=sz000002&p=1
     */
    private static final String API_URL = "https://stock.gtimg.cn/data/index.php";
    /**
     * 交易详情分割符
     */
    private static String DEAL_DETAIL_SPLIT_STR = "|";
    /**
     * 交易详情信息分割符
     */
    private static String DEAL_DETAIL_INFO_SPLIT_STR = "/";

    /**
     * 获取股票交易明细
     *
     * @param stockVo
     * @param page
     * @return
     */
    public List<TencentRealtimeDealDetailPo> dealDetail(StockVo stockVo, Integer page) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()
                || null == page || page < 0) {
            return null;
        }
        try {
            String tencnetStockCode = TencentBaseApi.tencentStockCode(stockVo);
            Map<String, Object> params = new HashMap<>(4);
            params.put("appn", "detail");
            params.put("action", "data");
            params.put("c", tencnetStockCode);
            params.put("p", page);
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParams(params)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            List<TencentRealtimeDealDetailPo> tencentRealtimeDealDetailPoList = this.handleResponse(
                    stockVo, page, httpResponse.getContent()
            );
            return tencentRealtimeDealDetailPoList;
        } catch (IOException e) {
            logger.error(page.toString());
            logger.error(stockVo.toString(), e);
        }
        return null;
    }

    /**
     * 解析返回数据
     *
     * @param stockVo
     * @param page
     * @param response
     * @return
     */
    private List<TencentRealtimeDealDetailPo> handleResponse(StockVo stockVo, Integer page, String response) {
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
            Integer responsePage = jsonArray.getInteger(0);
            String dealDetailStr = jsonArray.getString(1);
            if (null == responsePage || !responsePage.equals(page) || null == dealDetailStr || dealDetailStr.isEmpty()) {
                return null;
            }
            String[] dealDetailArr = StringUtils.split(dealDetailStr, DEAL_DETAIL_SPLIT_STR);
            List<TencentRealtimeDealDetailPo> tencentRealtimeDealDetailPoList = new ArrayList<>();
            for (int i = 0; i < dealDetailArr.length; i++) {
                String singleDealDetailStr = dealDetailArr[i];
                if (null == singleDealDetailStr || singleDealDetailStr.isEmpty()) {
                    continue;
                }
                TencentRealtimeDealDetailPo tencentRealtimeDealDetailPo =
                        handleSingleDealDetail(stockVo, singleDealDetailStr);
                if (null != tencentRealtimeDealDetailPo) {
                    tencentRealtimeDealDetailPoList.add(tencentRealtimeDealDetailPo);
                }
            }
            return tencentRealtimeDealDetailPoList;
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }

    /**
     * 处理单条交易详情
     *
     * @param stockVo
     * @param singleDealDetailStr
     * @return
     */
    private TencentRealtimeDealDetailPo handleSingleDealDetail(StockVo stockVo, String singleDealDetailStr) {
        if (null == singleDealDetailStr || singleDealDetailStr.isEmpty()) {
            return null;
        }
        String[] dealDetailInfoArr = StringUtils.split(singleDealDetailStr, DEAL_DETAIL_INFO_SPLIT_STR);
        TencentRealtimeDealDetailPo tencentRealtimeDealDetailPo = new TencentRealtimeDealDetailPo();
        for (int i = 0; i < dealDetailInfoArr.length; i++) {
            if (0 == i) {
                tencentRealtimeDealDetailPo.setNum(new BigDecimal(dealDetailInfoArr[i]).intValue());
            }
            if (1 == i) {
                tencentRealtimeDealDetailPo.setTime(dealDetailInfoArr[i]);
            }
            if (2 == i) {
                tencentRealtimeDealDetailPo.setPrice(BigDecimalUtil.initPrice(dealDetailInfoArr[i]));
            }
            if (3 == i) {
                tencentRealtimeDealDetailPo.setUptickPrice(BigDecimalUtil.initPrice(dealDetailInfoArr[i]));
            }
            if (4 == i) {
                tencentRealtimeDealDetailPo.setDealNum(handleDealNum(stockVo, dealDetailInfoArr[i]));
            }
            if (5 == i) {
                tencentRealtimeDealDetailPo.setDealMoney(BigDecimalUtil.initPrice(dealDetailInfoArr[i]));
            }
            if (6 == i) {
                tencentRealtimeDealDetailPo.setDealType(getDealType(dealDetailInfoArr[i]));
            }
        }
        return tencentRealtimeDealDetailPo;
    }
}
