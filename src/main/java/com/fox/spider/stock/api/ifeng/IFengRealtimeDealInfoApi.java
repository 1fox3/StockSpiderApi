package com.fox.spider.stock.api.ifeng;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.api.StockBaseApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.ifeng.IFengRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 凤凰网股票实时交易信息
 *
 * @author lusongsong
 * @date 2021/1/4 15:25
 */
@Component
public class IFengRealtimeDealInfoApi extends IFengBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 股票代码拼接字符串
     */
    private static String STOCK_CODE_SPLIT_STR = ",";
    /**
     * A股接口地址
     * https://hq.finance.ifeng.com/q.php?l=sh600683
     */
    private static final String A_API_URL = "https://hq.finance.ifeng.com/q.php";
    /**
     * 港股接口地址
     */
    private static final String HK_API_URL = "https://ifeng.szfuit.com:883/hkquote/api/query.php";
    /**
     * A股请求头参数
     */
    private static final String A_REFERER_URL = "https://finance.ifeng.com/";
    /**
     * 港股请求头参数
     */
    private static final String HK_REFERER_URL = "https://hk.finance.ifeng.com/";
    /**
     * 港股股票编号后缀
     */
    private static final String HK_STOCK_CODE_SUFFIX = "_dly";

    /**
     * 获取接口地址
     *
     * @param stockMarket
     * @return
     */
    private static final String getUrl(Integer stockMarket) {
        switch (stockMarket) {
            case StockConst.SM_HK:
                return HK_API_URL;
            default:
                return A_API_URL;
        }
    }

    /**
     * 获取peferer头部
     *
     * @param stockMarket
     * @return
     */
    private static final String getReferer(Integer stockMarket) {
        switch (stockMarket) {
            case StockConst.SM_HK:
                return HK_REFERER_URL;
            default:
                return A_REFERER_URL;
        }
    }

    /**
     * 将股票代码按照交易所分组
     *
     * @param stockVoList
     * @return
     */
    private Map<Integer, List<String>> getIFengStockCodeListMap(List<StockVo> stockVoList) {
        if (null == stockVoList || stockVoList.isEmpty()) {
            return null;
        }
        Map<Integer, List<String>> iFengStockCodeListMap = new HashMap<>();
        for (StockVo stockVo : stockVoList) {
            if (!iFengStockCodeListMap.containsKey(stockVo.getStockMarket())) {
                iFengStockCodeListMap.put(stockVo.getStockMarket(), new ArrayList<>());
            }
            String iFengStockCode = IFengBaseApi.iFengStockCode(stockVo);
            if (null != iFengStockCode && !iFengStockCode.isEmpty()) {
                iFengStockCodeListMap.get(stockVo.getStockMarket()).add(iFengStockCode);
            }
        }
        return iFengStockCodeListMap;
    }

    /**
     * 处理请求
     *
     * @param stockMarket
     * @param stockCodeList
     * @return
     */
    private Map<String, IFengRealtimeDealInfoPo> handleRequest(Integer stockMarket, List<String> stockCodeList) {
        if (null == stockMarket || null == stockCodeList || stockCodeList.isEmpty()) {
            return null;
        }
        String url = getUrl(stockMarket);
        String peferer = getReferer(stockMarket);
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.setUrl(url)
                .setHeader("Referer", peferer)
                .setOriCharset(HttpUtil.CHARSET_GBK)
                .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
        try {
            HttpResponseDto httpResponse;
            switch (stockMarket) {
                case StockConst.SM_HK:
                    httpUtil.setParam(
                            "key",
                            StringUtils.join(stockCodeList, HK_STOCK_CODE_SUFFIX + STOCK_CODE_SPLIT_STR) + HK_STOCK_CODE_SUFFIX
                    );
                    break;
                default:
                    httpUtil.setParam("l", StringUtils.join(stockCodeList, STOCK_CODE_SPLIT_STR));
                    break;
            }
            httpResponse = httpUtil.request();
            return this.handleResponse(stockMarket, httpResponse.getContent());
        } catch (IOException e) {
            logger.error(stockMarket.toString());
            logger.error(stockCodeList.toString());
            logger.error("handleRequest", e);
        }
        return null;
    }

    /**
     * 处理a股返回数据
     *
     * @param response
     * @return
     */
    private Map<String, IFengRealtimeDealInfoPo> handleResponse(Integer stockMarket, String response) {
        response = StockBaseApi.trimJsonObject(response);
        if (null == response) {
            return null;
        }
        try {
            JSONObject responseJO = JSONObject.parseObject(response);
            if (null == responseJO || responseJO.isEmpty()) {
                return null;
            }
            Map<String, IFengRealtimeDealInfoPo> iFengRealtimeDealInfoPoMap = new HashMap<>(responseJO.keySet().size());
            for (String iFengStockCode : responseJO.keySet()) {
                StockVo stockVo = iFengStockCodeToStockVo(iFengStockCode);
                IFengRealtimeDealInfoPo iFengRealtimeDealInfoPo = null;
                switch (stockMarket) {
                    case StockConst.SM_HK:
                        JSONObject currentJO = responseJO.getJSONObject(iFengStockCode);
                        iFengRealtimeDealInfoPo = hkHandleDealInfo(stockVo, currentJO);
                        break;
                    default:
                        JSONArray responseJA = responseJO.getJSONArray(iFengStockCode);
                        iFengRealtimeDealInfoPo = aHandleDealInfo(stockVo, responseJA);
                        break;
                }
                if (null != iFengRealtimeDealInfoPo) {
                    iFengRealtimeDealInfoPoMap.put(stockVo.getStockCode(), iFengRealtimeDealInfoPo);
                }
            }
            return iFengRealtimeDealInfoPoMap;
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }

    /**
     * 处理A股交易信息
     *
     * @param stockVo
     * @param jsonArray
     * @return
     */
    private IFengRealtimeDealInfoPo aHandleDealInfo(StockVo stockVo, JSONArray jsonArray) {
        if (null == stockVo || null == jsonArray || jsonArray.isEmpty()) {
            return null;
        }
        IFengRealtimeDealInfoPo iFengRealtimeDealInfoPo = new IFengRealtimeDealInfoPo();
        iFengRealtimeDealInfoPo.setStockMarket(stockVo.getStockMarket());
        iFengRealtimeDealInfoPo.setStockCode(stockVo.getStockCode());
        List<String> unknownList = new ArrayList<>();
        List<BigDecimal> priceList = new ArrayList<>();
        List<Long> numList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            String dealInfoStr = jsonArray.getString(i);
            if (null == dealInfoStr || dealInfoStr.isEmpty()) {
                continue;
            }
            if (0 == i) {
                iFengRealtimeDealInfoPo.setCurrentPrice(BigDecimalUtil.initPrice(dealInfoStr));
            } else if (1 == i) {
                iFengRealtimeDealInfoPo.setPreClosePrice(BigDecimalUtil.initPrice(dealInfoStr));
            } else if (2 == i) {
                iFengRealtimeDealInfoPo.setUptickPrice(BigDecimalUtil.initPrice(dealInfoStr));
            } else if (3 == i) {
                iFengRealtimeDealInfoPo.setUptickRate(BigDecimalUtil.initRate(dealInfoStr));
            } else if (4 == i) {
                iFengRealtimeDealInfoPo.setOpenPrice(BigDecimalUtil.initPrice(dealInfoStr));
            } else if (5 == i) {
                iFengRealtimeDealInfoPo.setHighestPrice(BigDecimalUtil.initPrice(dealInfoStr));
            } else if (6 == i) {
                iFengRealtimeDealInfoPo.setLowestPrice(BigDecimalUtil.initPrice(dealInfoStr));
            } else if (9 == i) {
                iFengRealtimeDealInfoPo.setDealNum(BigDecimalUtil.initLong(dealInfoStr));
            } else if (10 == i) {
                iFengRealtimeDealInfoPo.setDealMoney(BigDecimalUtil.initPrice(dealInfoStr));
            } else if (11 <= i && 30 >= i) {
                //买
                if (11 <= i && 15 >= i) {
                    priceList.add(BigDecimalUtil.initPrice(dealInfoStr));
                    //卖
                } else if (21 <= i && 25 >= i) {
                    priceList.add(BigDecimalUtil.initPrice(dealInfoStr));
                } else {
                    numList.add(BigDecimalUtil.initLong(dealInfoStr));
                    if (20 == i || 30 == i) {
                        LinkedHashMap<BigDecimal, Long> priceNumMap = new LinkedHashMap<>();
                        for (int j = 0; j < priceList.size(); j++) {
                            priceNumMap.put(priceList.get(j), numList.get(j));
                        }
                        if (20 == i) {
                            iFengRealtimeDealInfoPo.setBuyPriceMap(priceNumMap);
                        } else {
                            iFengRealtimeDealInfoPo.setSellPriceMap(priceNumMap);
                        }
                        priceList = new ArrayList<>();
                        numList = new ArrayList<>();
                    }
                }
            } else if (34 == i) {
                Long timestamp = BigDecimalUtil.initLong(dealInfoStr) * 1000;
                iFengRealtimeDealInfoPo.setDt(DateUtil.timestampFormat(timestamp, DateUtil.DATE_FORMAT_1));
                iFengRealtimeDealInfoPo.setTime(DateUtil.timestampFormat(timestamp, DateUtil.TIME_FORMAT_2));
            } else {
                unknownList.add(dealInfoStr);
            }
        }
        if (null != unknownList && !unknownList.isEmpty()) {
            iFengRealtimeDealInfoPo.setUnknownKeyList(unknownList);
        }
        return iFengRealtimeDealInfoPo;
    }

    /**
     * 处理港股交易信息
     *
     * @param stockVo
     * @param jsonObject
     * @return
     */
    private IFengRealtimeDealInfoPo hkHandleDealInfo(StockVo stockVo, JSONObject jsonObject) {
        if (null == stockVo || null == jsonObject || jsonObject.isEmpty()) {
            return null;
        }
        IFengRealtimeDealInfoPo iFengRealtimeDealInfoPo = new IFengRealtimeDealInfoPo();
        iFengRealtimeDealInfoPo.setStockMarket(stockVo.getStockMarket());
        iFengRealtimeDealInfoPo.setStockCode(stockVo.getStockCode());
        if (jsonObject.containsKey("last")) {
            iFengRealtimeDealInfoPo.setCurrentPrice(BigDecimalUtil.initPrice(jsonObject.getString("last")));
        }
        if (jsonObject.containsKey("name")) {
            iFengRealtimeDealInfoPo.setStockName(jsonObject.getString("name"));
        }
        if (jsonObject.containsKey("hst_close")) {
            iFengRealtimeDealInfoPo.setPreClosePrice(BigDecimalUtil.initPrice(jsonObject.getString("hst_close")));
        }
        if (jsonObject.containsKey("chg")) {
            iFengRealtimeDealInfoPo.setUptickPrice(BigDecimalUtil.initPrice(jsonObject.getString("chg")));
        }
        if (jsonObject.containsKey("chg_pct")) {
            iFengRealtimeDealInfoPo.setUptickPrice(BigDecimalUtil.initRate(jsonObject.getString("chg_pct")));
        }
        if (jsonObject.containsKey("open")) {
            iFengRealtimeDealInfoPo.setOpenPrice(BigDecimalUtil.initPrice(jsonObject.getString("open")));
        }
        if (jsonObject.containsKey("high")) {
            iFengRealtimeDealInfoPo.setHighestPrice(BigDecimalUtil.initPrice(jsonObject.getString("high")));
        }
        if (jsonObject.containsKey("low")) {
            iFengRealtimeDealInfoPo.setLowestPrice(BigDecimalUtil.initPrice(jsonObject.getString("low")));
        }
        if (jsonObject.containsKey("volume")) {
            iFengRealtimeDealInfoPo.setDealNum(BigDecimalUtil.initLong(jsonObject.getString("volume")));
        }
        if (jsonObject.containsKey("amount")) {
            iFengRealtimeDealInfoPo.setDealMoney(BigDecimalUtil.initPrice(jsonObject.getString("amount")));
        }
        if (jsonObject.containsKey("mkt_time")) {
            iFengRealtimeDealInfoPo.setDt(DateUtil.dateStrFormatChange(jsonObject.getString("mkt_time"), DateUtil.TIME_FORMAT_1, DateUtil.DATE_FORMAT_1));
            iFengRealtimeDealInfoPo.setTime(DateUtil.dateStrFormatChange(jsonObject.getString("mkt_time"), DateUtil.TIME_FORMAT_1, DateUtil.TIME_FORMAT_2));
        }
        return iFengRealtimeDealInfoPo;
    }

    /**
     * 获取股票的实时交易数据
     *
     * @param stockVo
     * @return
     */
    public IFengRealtimeDealInfoPo realtimeDealInfo(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockCode() || null == stockVo.getStockMarket()) {
            return null;
        }
        List<StockVo> stockVoList = Arrays.asList(stockVo);
        Map<String, IFengRealtimeDealInfoPo> iFengRealtimeDealInfoPoMap = this.batchRealtimeDealInfo(stockVoList);
        if (null != iFengRealtimeDealInfoPoMap && iFengRealtimeDealInfoPoMap.containsKey(stockVo.getStockCode())) {
            return iFengRealtimeDealInfoPoMap.get(stockVo.getStockCode());
        }
        return null;
    }

    /**
     * 批量获取股票的实时交易数据
     *
     * @param stockVoList
     * @return
     */
    public Map<String, IFengRealtimeDealInfoPo> batchRealtimeDealInfo(List<StockVo> stockVoList) {
        if (null == stockVoList || stockVoList.isEmpty()) {
            return null;
        }
        try {
            Map<Integer, List<String>> stockCodeListMap = getIFengStockCodeListMap(stockVoList);
            if (null == stockCodeListMap || stockCodeListMap.isEmpty()) {
                return null;
            }
            Map<String, IFengRealtimeDealInfoPo> iFengRealtimeDealInfoPoMap = new HashMap<>();
            for (Integer stockMarket : stockCodeListMap.keySet()) {
                List<String> stockCodeList = stockCodeListMap.get(stockMarket);
                if (null == stockCodeList || stockCodeList.isEmpty()) {
                    continue;
                }
                Map<String, IFengRealtimeDealInfoPo> smIFengRealtimeDealInfoPoMap =
                        handleRequest(stockMarket, stockCodeList);
                if (null != smIFengRealtimeDealInfoPoMap) {
                    iFengRealtimeDealInfoPoMap.putAll(smIFengRealtimeDealInfoPoMap);
                }
            }
            return iFengRealtimeDealInfoPoMap;
        } catch (Exception e) {
            logger.error("batchRealtimeDealInfo", e);
        }
        return null;
    }
}
