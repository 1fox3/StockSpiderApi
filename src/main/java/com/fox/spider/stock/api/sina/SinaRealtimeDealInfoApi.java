package com.fox.spider.stock.api.sina;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 股票实时成交信息
 *
 * @author lusongsong
 * @date 2020/11/5 14:31
 */
@Component
public class SinaRealtimeDealInfoApi extends SinaBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口链接
     * http://hq.sinajs.cn/list=sh603383,sh601519
     */
    /**
     * 接口地址
     */
    private static String apiUrl = "http://hq.sinajs.cn/list=";
    /**
     * 返回数据跟分割符
     */
    private static String stockInfoSplitStr = ";";
    private static String infoSplitStr = "\"";
    private static String dataSplitStr = ",";

    /**
     * 返回样例,最后一个字段解释（00:正常,03:停牌,07:临时停牌,-2:未上市新股, -3:(未知明确含义，但当日无交易)）
     * var hq_str_sh603383="顶点软件,75.300,73.200,74.160,75.300,73.200,74.160,74.170,1441855,106849717.000,3900,74.160,1400,74.150,200,74.120,1900,74.100,1600,74.090,4600,74.170,1500,74.180,1200,74.200,300,74.330,100,74.400,2019-12-24,15:00:00,00,";
     * var hq_str_sh601519="大智慧,7.930,7.860,7.980,8.050,7.860,7.970,7.980,47836338,380201052.000,544700,7.970,181700,7.960,260474,7.950,89698,7.940,108200,7.930,73500,7.980,88100,7.990,539700,8.000,92700,8.010,247800,8.020,2019-12-24,15:00:03,00,";
     */

    /**
     * 获取单只股票的实时交易数据
     *
     * @param stockVo
     * @return
     */
    public SinaRealtimeDealInfoPo realtimeDealInfo(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockCode() || null == stockVo.getStockMarket()) {
            return null;
        }
        List<StockVo> stockVoList = Arrays.asList(stockVo);
        Map<String, SinaRealtimeDealInfoPo> sinaRealtimeDealInfoPoMap = this.batchRealtimeDealInfo(stockVoList);
        if (sinaRealtimeDealInfoPoMap.containsKey(stockVo.getStockCode())) {
            return sinaRealtimeDealInfoPoMap.get(stockVo.getStockCode());
        }
        return null;
    }

    /**
     * 获取批量股票的实时交易数据
     *
     * @param stockVoList
     * @return
     */
    public Map<String, SinaRealtimeDealInfoPo> batchRealtimeDealInfo(List<StockVo> stockVoList) {
        if (null == stockVoList || stockVoList.isEmpty()) {
            return new HashMap<>(0);
        }
        HashMap<String, SinaRealtimeDealInfoPo> sinaRealtimeDealInfoPoHashMap = new HashMap<>(stockVoList.size());
        try {
            List<String> sinaStockCodeList = new ArrayList<>();
            for (StockVo stockVo : stockVoList) {
                String sinaStockCode = SinaBaseApi.sinaStockCode(stockVo);
                if (null != sinaStockCode && !sinaStockCode.isEmpty()) {
                    sinaStockCodeList.add(sinaStockCode);
                }
            }
            if (sinaStockCodeList.isEmpty()) {
                return new HashMap<>(0);
            }
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(apiUrl + StringUtils.join(sinaStockCodeList, dataSplitStr))
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return sinaRealtimeDealInfoPoHashMap;
    }

    /**
     * 处理接口返回
     *
     * @param response
     * @return
     */
    private Map<String, SinaRealtimeDealInfoPo> handleResponse(String response) {
        if (response.contains(stockInfoSplitStr)) {
            String[] responseArr = response.trim().split(stockInfoSplitStr);
            HashMap<String, SinaRealtimeDealInfoPo> sinaRealtimeDealInfoPoHashMap = new HashMap<>(responseArr.length);
            for (int i = 0; i < responseArr.length; i++) {
                if (!responseArr[i].equals("")) {
                    String stockCodeStr = getStockCodeStr(responseArr[i]);
                    String stockCode = getStockCode(stockCodeStr);
                    SinaRealtimeDealInfoPo sinaRealtimeDealInfoPo = getSinaRealtimeDealInfo(responseArr[i]);
                    if (null != stockCode && !stockCode.isEmpty() && null != sinaRealtimeDealInfoPo) {
                        sinaRealtimeDealInfoPo.setStockCode(stockCode);
                        sinaRealtimeDealInfoPoHashMap.put(stockCode, sinaRealtimeDealInfoPo);
                    }
                }
            }
            return sinaRealtimeDealInfoPoHashMap;
        }
        return new HashMap<>(0);
    }

    /**
     * 获取新浪股票代码
     *
     * @param response
     * @return
     */
    private static String getStockCodeStr(String response) {
        response = response.trim();
        int index = response.lastIndexOf("=");
        response = response.substring(0, index);
        response = response.replace("var hq_str_", "");
        return response;
    }

    /**
     * 获取返回中的股票编号
     *
     * @param stockCodeStr
     * @return
     */
    private static String getStockCode(String stockCodeStr) {
        for (String sinaStockPreCode : SinaBaseApi.stockMarketPYMap.values()) {
            if (stockCodeStr.startsWith(sinaStockPreCode)) {
                stockCodeStr = stockCodeStr.replace(sinaStockPreCode, "");
            }
        }
        return stockCodeStr;
    }

    /**
     * 获取股票集市
     *
     * @param stockCodeStr
     * @return
     */
    private static Integer getStockMarket(String stockCodeStr) {
        for (Integer stockMarket : SinaBaseApi.stockMarketPYMap.keySet()) {
            if (stockCodeStr.startsWith(SinaBaseApi.stockMarketPYMap.get(stockMarket))) {
                return stockMarket;
            }
        }
        return null;
    }

    /**
     * 获取股票数据实体
     *
     * @param response
     * @return
     */
    private SinaRealtimeDealInfoPo getSinaRealtimeDealInfo(String response) {
        SinaRealtimeDealInfoPo sinaRealtimeDealInfoPo = new SinaRealtimeDealInfoPo();
        String stockCodeStr = getStockCodeStr(response);
        int startIndex = response.indexOf(infoSplitStr);
        int endIndex = response.lastIndexOf(infoSplitStr);
        if (startIndex > 0 && endIndex > 0) {
            response = response.substring(startIndex + 1, endIndex);
            if (response.contains(dataSplitStr)) {
                String[] responseArr = response.split(dataSplitStr);
                if (StockConst.SM_HK == getStockMarket(stockCodeStr)) {
                    sinaRealtimeDealInfoPo = hkDealInfo(responseArr);
                } else {
                    sinaRealtimeDealInfoPo = aDealInfo(responseArr);
                }
            } else {
                return null;
            }
        }
        return sinaRealtimeDealInfoPo;
    }

    /**
     * 构建中国股票信息
     *
     * @param responseArr
     * @return
     */
    private SinaRealtimeDealInfoPo aDealInfo(String[] responseArr) {
        SinaRealtimeDealInfoPo sinaRealtimeDealInfoPo = new SinaRealtimeDealInfoPo();
        try {
            if (null == responseArr || responseArr.length == 0) {
                return sinaRealtimeDealInfoPo;
            }
            Map<BigDecimal, Map<String, BigDecimal>> sellList = new LinkedHashMap<>(5);
            List<BigDecimal> sellPriceList = new ArrayList<>();
            Map<BigDecimal, Map<String, BigDecimal>> buyList = new LinkedHashMap<>(5);
            List<BigDecimal> buyPriceList = new ArrayList<>();
            Map<String, BigDecimal> temp = new LinkedHashMap<>();
            List<String> unknownList = new LinkedList<>();
            for (int i = 0; i < responseArr.length; i++) {
                if (responseArr[i].equals("")) {
                    continue;
                }
                if (0 == i) {
                    sinaRealtimeDealInfoPo.setStockName(responseArr[i].replace(" ", ""));
                }
                if (1 == i) {
                    sinaRealtimeDealInfoPo.setOpenPrice(
                            BigDecimalUtil.initPrice(responseArr[i])
                    );
                }
                if (2 == i) {
                    sinaRealtimeDealInfoPo.setPreClosePrice(
                            BigDecimalUtil.initPrice(responseArr[i])
                    );
                }
                if (3 == i) {
                    sinaRealtimeDealInfoPo.setCurrentPrice(
                            BigDecimalUtil.initPrice(responseArr[i])
                    );
                }
                if (4 == i) {
                    sinaRealtimeDealInfoPo.setHighestPrice(
                            BigDecimalUtil.initPrice(responseArr[i])
                    );
                }
                if (5 == i) {
                    sinaRealtimeDealInfoPo.setLowestPrice(
                            BigDecimalUtil.initPrice(responseArr[i])
                    );
                }
                if (6 == i) {
                    sinaRealtimeDealInfoPo.setCompeteBuyPrice(
                            BigDecimalUtil.initPrice(responseArr[i])
                    );
                }
                if (7 == i) {
                    sinaRealtimeDealInfoPo.setCompeteSellPrice(
                            BigDecimalUtil.initPrice(responseArr[i])
                    );
                }
                if (8 == i) {
                    sinaRealtimeDealInfoPo.setDealNum(Long.valueOf(responseArr[i]));
                }
                if (9 == i) {
                    sinaRealtimeDealInfoPo.setDealMoney(
                            BigDecimalUtil.initPrice(responseArr[i])
                    );
                }
                if (10 <= i && 29 >= i) {
                    if (0 == i % 2) {
                        temp.put("num", new BigDecimal(responseArr[i]));
                    } else {
                        temp.put("price", BigDecimalUtil.initPrice(responseArr[i]));
                        if (10 <= i && 19 >= i) {
                            buyList.put(temp.get("price"), temp);
                            buyPriceList.add(temp.get("price"));
                        } else {
                            sellList.put(temp.get("price"), temp);
                            sellPriceList.add(temp.get("price"));
                        }
                        temp = new LinkedHashMap<>();
                    }
                }
                if (30 == i) {
                    sinaRealtimeDealInfoPo.setDt(responseArr[i]);
                }
                if (31 == i) {
                    sinaRealtimeDealInfoPo.setTime(responseArr[i]);
                }
                if (32 == i) {
                    sinaRealtimeDealInfoPo.setDealStatus(responseArr[i]);
                }
                if (33 <= i) {
                    unknownList.add(responseArr[i]);
                }
            }
            if (sellList.size() > 0) {
                Collections.reverse(sellPriceList);
                List<Map<String, BigDecimal>> list = new LinkedList<>();
                for (BigDecimal price : sellPriceList) {
                    list.add(sellList.get(price));
                }
                sinaRealtimeDealInfoPo.setSellPriceList(list);
            }
            if (buyList.size() > 0) {
                Collections.sort(buyPriceList);
                List<Map<String, BigDecimal>> list = new LinkedList<>();
                for (BigDecimal price : buyPriceList) {
                    list.add(0, buyList.get(price));
                }
                sinaRealtimeDealInfoPo.setBuyPriceList(list);
            }
            if (unknownList.size() > 0) {
                sinaRealtimeDealInfoPo.setUnknownKeyList(unknownList);
            }
            //昨日收盘价
            BigDecimal preClosePrice = sinaRealtimeDealInfoPo.getPreClosePrice();
            //当前价格
            BigDecimal currentPrice = sinaRealtimeDealInfoPo.getCurrentPrice();
            if (null == currentPrice || null == preClosePrice
                    || 0 == preClosePrice.compareTo(BigDecimal.ZERO)
            ) {
                return sinaRealtimeDealInfoPo;
            }
            BigDecimal uptickPrice = currentPrice.subtract(preClosePrice);
            sinaRealtimeDealInfoPo.setUptickPrice(uptickPrice);
            BigDecimal uptickRate = uptickPrice.multiply(new BigDecimal(100)).divide(preClosePrice, 2, RoundingMode.HALF_UP);
            sinaRealtimeDealInfoPo.setUptickRate(uptickRate);
        } catch (Exception e) {
            for (int i = 0; i < responseArr.length; i++) {
                logger.error(responseArr[i]);
            }
            logger.error(e.getClass().getSimpleName());
            logger.error(e.getMessage());
        }
        return sinaRealtimeDealInfoPo;
    }

    /**
     * 构建香港股票信息
     *
     * @param responseArr
     * @return
     */
    private SinaRealtimeDealInfoPo hkDealInfo(String[] responseArr) {
        SinaRealtimeDealInfoPo sinaRealtimeDealInfoPo = new SinaRealtimeDealInfoPo();
        try {
            if (null == responseArr || responseArr.length == 0) {
                return sinaRealtimeDealInfoPo;
            }
            List<String> unknownList = new LinkedList<>();
            for (int i = 0; i < responseArr.length; i++) {
                if (responseArr[i].equals("")) {
                    continue;
                }
                if (0 == i) {
                    sinaRealtimeDealInfoPo.setStockNameEn(responseArr[i]);
                }
                if (1 == i) {
                    sinaRealtimeDealInfoPo.setStockName(responseArr[i].replace(" ", ""));
                }
                if (2 == i) {
                    sinaRealtimeDealInfoPo.setOpenPrice(BigDecimalUtil.initPrice(responseArr[i]));
                }
                if (3 == i) {
                    sinaRealtimeDealInfoPo.setPreClosePrice(BigDecimalUtil.initPrice(responseArr[i]));
                }
                if (4 == i) {
                    sinaRealtimeDealInfoPo.setHighestPrice(BigDecimalUtil.initPrice(responseArr[i]));
                }
                if (5 == i) {
                    sinaRealtimeDealInfoPo.setLowestPrice(BigDecimalUtil.initPrice(responseArr[i]));
                }
                if (6 == i) {
                    sinaRealtimeDealInfoPo.setCurrentPrice(BigDecimalUtil.initPrice(responseArr[i]));
                }
                if (7 == i) {
                    sinaRealtimeDealInfoPo.setUptickPrice(BigDecimalUtil.initPrice(responseArr[i]));
                }
                if (8 == i) {
                    sinaRealtimeDealInfoPo.setUptickRate(BigDecimalUtil.initPrice(responseArr[i]));
                }
                if (9 == i) {
                    sinaRealtimeDealInfoPo.setMinuteLowestPrice(BigDecimalUtil.initPrice(responseArr[i]));
                }
                if (10 == i) {
                    sinaRealtimeDealInfoPo.setMinuteHighestPrice(BigDecimalUtil.initPrice(responseArr[i]));
                }
                if (11 == i) {
                    sinaRealtimeDealInfoPo.setDealMoney(BigDecimalUtil.initPrice(responseArr[i]));
                }
                if (12 == i) {
                    sinaRealtimeDealInfoPo.setDealNum(Long.valueOf(responseArr[i]));
                }
                if (13 <= i && 16 >= i) {
                    //i==15或16时，近期的最高，底价，时间范围不定，暂时观察至少是近一年的
                    unknownList.add(responseArr[i]);
                }
                if (17 == i) {
                    sinaRealtimeDealInfoPo.setDt(responseArr[i].replace("/", "-"));
                }
                if (18 == i) {
                    sinaRealtimeDealInfoPo.setTime(responseArr[i] + ":00");
                }
            }
            if (unknownList.size() > 0) {
                sinaRealtimeDealInfoPo.setUnknownKeyList(unknownList);
            }
        } catch (Exception e) {
            for (int i = 0; i < responseArr.length; i++) {
                logger.error(responseArr[i]);
            }
            logger.error(e.getClass().getSimpleName());
            logger.error(e.getMessage());
        }
        return sinaRealtimeDealInfoPo;
    }
}
