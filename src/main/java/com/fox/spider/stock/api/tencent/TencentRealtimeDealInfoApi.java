package com.fox.spider.stock.api.tencent;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.tencent.TencentRealtimeDealInfoPo;
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
import java.math.RoundingMode;
import java.util.*;

/**
 * 腾讯股票实时成交信息
 *
 * @author lusongsong
 * @date 2020/12/24 13:38
 */
@Component
public class TencentRealtimeDealInfoApi extends TencentBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * https://web.sqt.gtimg.cn/q=sh603383,sh601519
     */
    private static final String API_URL = "https://web.sqt.gtimg.cn/q=";
    /**
     * 股票代码拼接字符串
     */
    private static String STOCK_CODE_SPLIT_STR = ",";
    /**
     * 返回数据分割符
     */
    private static String RESPONSE_SPLIT_STR = ";";
    /**
     * 交易数据分割符
     */
    private static String DEAL_INFO_SPLIT_STR = "~";
    /**
     * 交易数据起止符
     */
    private static String DEAL_INFO_STR = "\"";

    /**
     * 获取股票的实时交易数据
     *
     * @param stockVo
     * @return
     */
    public TencentRealtimeDealInfoPo realtimeDealInfo(StockVo stockVo) {
        if (null == stockVo || null == stockVo.getStockCode() || null == stockVo.getStockMarket()) {
            return null;
        }
        List<StockVo> stockVoList = Arrays.asList(stockVo);
        Map<String, TencentRealtimeDealInfoPo> tencentRealtimeDealInfoPoMap = this.batchRealtimeDealInfo(stockVoList);
        if (null != tencentRealtimeDealInfoPoMap && tencentRealtimeDealInfoPoMap.containsKey(stockVo.getStockCode())) {
            return tencentRealtimeDealInfoPoMap.get(stockVo.getStockCode());
        }
        return null;
    }

    /**
     * 批量获取股票的实时交易数据
     *
     * @param stockVoList
     * @return
     */
    public Map<String, TencentRealtimeDealInfoPo> batchRealtimeDealInfo(List<StockVo> stockVoList) {
        if (null == stockVoList || stockVoList.isEmpty()) {
            return null;
        }
        try {
            List<String> tencnetStockCodeList = new ArrayList<>();
            for (StockVo stockVo : stockVoList) {
                String tencnetStockCode = TencentBaseApi.tencentStockCode(stockVo);
                if (null != tencnetStockCode && !tencnetStockCode.isEmpty()) {
                    tencnetStockCodeList.add(tencnetStockCode);
                }
            }
            if (tencnetStockCodeList.isEmpty()) {
                return null;
            }
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(API_URL + StringUtils.join(tencnetStockCodeList, STOCK_CODE_SPLIT_STR))
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            return this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            logger.error("batchRealtimeDealInfo", e);
        }
        return null;
    }

    /**
     * 处理接口返回数据
     *
     * @param response
     * @return
     */
    private Map<String, TencentRealtimeDealInfoPo> handleResponse(String response) {
        if (null != response && !response.isEmpty() && response.contains(RESPONSE_SPLIT_STR)) {
            String[] responseArr = response.trim().split(RESPONSE_SPLIT_STR);
            HashMap<String, TencentRealtimeDealInfoPo> tencentRealtimeDealInfoPoMap = new HashMap<>(responseArr.length);
            for (int i = 0; i < responseArr.length; i++) {
                String singleResponse = responseArr[i];
                if (null == singleResponse || singleResponse.isEmpty() || !response.contains(DEAL_INFO_SPLIT_STR)) {
                    continue;
                }
                String stockCodeStr = getStockCodeStr(singleResponse);
                StockVo stockVo = tencnetStockCodeToStockVo(stockCodeStr);
                int startIndex = singleResponse.indexOf(DEAL_INFO_STR);
                int endIndex = singleResponse.lastIndexOf(DEAL_INFO_STR);
                if (startIndex > 0 && endIndex > 0) {
                    singleResponse = singleResponse.substring(startIndex + 1, endIndex);
                    TencentRealtimeDealInfoPo tencentRealtimeDealInfoPo = getDealInfo(stockVo, singleResponse);
                    if (null == tencentRealtimeDealInfoPo) {
                        continue;
                    }
                    tencentRealtimeDealInfoPo.setStockMarket(stockVo.getStockMarket());
                    tencentRealtimeDealInfoPoMap.put(stockVo.getStockCode(), tencentRealtimeDealInfoPo);
                }
            }
            return tencentRealtimeDealInfoPoMap;
        }
        return null;
    }

    /**
     * 获取腾讯股票代码
     *
     * @param response
     * @return
     */
    private static String getStockCodeStr(String response) {
        response = response.trim();
        int index = response.lastIndexOf("=");
        response = response.substring(0, index);
        response = response.replace("v_", "");
        return response;
    }

    /**
     * 获取实时交易信息
     *
     * @param stockVo
     * @param dealInfoStr
     * @return
     */
    private TencentRealtimeDealInfoPo getDealInfo(StockVo stockVo, String dealInfoStr) {
        if (null == stockVo || null == dealInfoStr || dealInfoStr.isEmpty()) {
            return null;
        }
        switch (stockVo.getStockMarket()) {
            case StockConst.SM_SH:
            case StockConst.SM_SZ:
                return getADealInfo(dealInfoStr);
            case StockConst.SM_HK:
                return getHKDealInfo(dealInfoStr);
            default:
                return null;
        }
    }

    /**
     * 获取A股交易信息
     *
     * @param dealInfoStr
     * @return
     */
    private TencentRealtimeDealInfoPo getADealInfo(String dealInfoStr) {
        if (null == dealInfoStr || dealInfoStr.isEmpty()) {
            return null;
        }

        String[] dealInfoArr = dealInfoStr.split(DEAL_INFO_SPLIT_STR);
        TencentRealtimeDealInfoPo tencentRealtimeDealInfoPo = new TencentRealtimeDealInfoPo();
        List<String> unknownList = new ArrayList<>();
        //售价
        BigDecimal price = null;
        //售量
        Integer num = null;
        LinkedHashMap<BigDecimal, Integer> priceMap = new LinkedHashMap(5);
        for (int i = 1; i < dealInfoArr.length; i++) {
            String dealInfo = dealInfoArr[i];
            if (null == dealInfo || dealInfo.isEmpty()) {
                continue;
            }
            if (1 == i) {
                tencentRealtimeDealInfoPo.setStockName(dealInfo);
            } else if (2 == i) {
                tencentRealtimeDealInfoPo.setStockCode(dealInfo);
            } else if (3 == i) {
                tencentRealtimeDealInfoPo.setCurrentPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (4 == i) {
                tencentRealtimeDealInfoPo.setPreClosePrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (5 == i) {
                tencentRealtimeDealInfoPo.setOpenPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (6 == i || 36 == i) {
                //手
                tencentRealtimeDealInfoPo.setDealNum(Long.valueOf(dealInfo));
            } else if (9 <= i && 28 >= i) {
                if (0 == i % 2) {
                    num = Integer.valueOf(dealInfo);
                    priceMap.put(price, num);
                    if (i == 18) {
                        tencentRealtimeDealInfoPo.setBuyPriceMap(priceMap);
                        priceMap = new LinkedHashMap<>(5);
                    }
                    if (i == 28) {
                        tencentRealtimeDealInfoPo.setSellPriceMap(priceMap);
                    }
                } else {
                    price = BigDecimalUtil.initPrice(dealInfo);
                }
            } else if (30 == i) {
                tencentRealtimeDealInfoPo.setDt(
                        DateUtil.dateStrFormatChange(dealInfo, DateUtil.TIME_FORMAT_3, DateUtil.DATE_FORMAT_1)
                );
                tencentRealtimeDealInfoPo.setTime(
                        DateUtil.dateStrFormatChange(dealInfo, DateUtil.TIME_FORMAT_3, DateUtil.TIME_FORMAT_2)
                );
            } else if (31 == i) {
                tencentRealtimeDealInfoPo.setUptickPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (32 == i) {
                //*100
                tencentRealtimeDealInfoPo.setUptickRate(BigDecimalUtil.initPrice(dealInfo));
            } else if (33 == i || 41 == i) {
                tencentRealtimeDealInfoPo.setHighestPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (34 == i || 42 == i) {
                tencentRealtimeDealInfoPo.setLowestPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (35 == i) {
                String[] dealArr = dealInfo.split("/");
                for (int j = 0; j < dealArr.length; j++) {
                    switch (j) {
                        case 0:
                            tencentRealtimeDealInfoPo.setCurrentPrice(BigDecimalUtil.initPrice(dealArr[j]));
                            break;
                        case 1:
                            //手
                            tencentRealtimeDealInfoPo.setDealNum(Long.valueOf(dealArr[j]));
                            break;
                        case 2:
                            tencentRealtimeDealInfoPo.setDealMoney(BigDecimalUtil.initPrice(dealArr[j]));
                            break;
                        default:
                            break;
                    }
                }
            } else if (37 == i) {
                //成交金额(单位：万，整数)
            } else if (38 == i) {
                //*100
                tencentRealtimeDealInfoPo.setTurnoverRate(BigDecimalUtil.initPrice(dealInfo));
            } else if (39 == i) {
                //*100
                tencentRealtimeDealInfoPo.setPerRate(BigDecimalUtil.initPrice(dealInfo));
            } else if (43 == i) {
                //*100
                tencentRealtimeDealInfoPo.setSurgeRate(BigDecimalUtil.initPrice(dealInfo));
            } else if (44 == i) {
                //亿
                tencentRealtimeDealInfoPo.setCircValue(BigDecimalUtil.initPrice(dealInfo));
            } else if (45 == i) {
                //亿
                tencentRealtimeDealInfoPo.setTotalValue(BigDecimalUtil.initPrice(dealInfo));
            } else if (46 == i) {
                //*100
                tencentRealtimeDealInfoPo.setPbrRate(BigDecimalUtil.initPrice(dealInfo));
            } else if (47 == i) {
                tencentRealtimeDealInfoPo.setUpLimitPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (48 == i) {
                tencentRealtimeDealInfoPo.setDownLimitPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (49 == i) {
                //*100
                tencentRealtimeDealInfoPo.setQrrRate(BigDecimalUtil.initPrice(dealInfo));
            } else if (50 == i) {
                tencentRealtimeDealInfoPo.setCommitteeSent(Long.valueOf(dealInfo));
            } else if (51 == i) {
                tencentRealtimeDealInfoPo.setAvgPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (52 == i) {
                //*100
                tencentRealtimeDealInfoPo.setPerDynamicRate(BigDecimalUtil.initPrice(dealInfo));
            } else if (53 == i) {
                //*100
                tencentRealtimeDealInfoPo.setPerStaticRate(BigDecimalUtil.initPrice(dealInfo));
            } else if (57 == i) {
                //成交金额(单位：万，保留4位小数)
            } else if (58 == i) {
                //万
                tencentRealtimeDealInfoPo.setCloseDealMoney(new BigDecimal(dealInfo).setScale(4, RoundingMode.HALF_UP));
            } else if (59 == i) {
                //手
                tencentRealtimeDealInfoPo.setCloseDealNum(Long.valueOf(dealInfo));
            } else {
                unknownList.add(dealInfo);
            }
        }
        tencentRealtimeDealInfoPo.setUnknownKeyList(unknownList);
        return tencentRealtimeDealInfoPo;
    }

    /**
     * 获取港股交易信息
     *
     * @param dealInfoStr
     * @return
     */
    private TencentRealtimeDealInfoPo getHKDealInfo(String dealInfoStr) {
        if (null == dealInfoStr || dealInfoStr.isEmpty()) {
            return null;
        }

        String[] dealInfoArr = dealInfoStr.split(DEAL_INFO_SPLIT_STR);
        TencentRealtimeDealInfoPo tencentRealtimeDealInfoPo = new TencentRealtimeDealInfoPo();
        List<String> unknownList = new ArrayList<>();
        //售价
        BigDecimal price = null;
        //售量
        Integer num = null;
        LinkedHashMap<BigDecimal, Integer> priceMap = new LinkedHashMap(5);
        for (int i = 1; i < dealInfoArr.length; i++) {
            String dealInfo = dealInfoArr[i];
            if (null == dealInfo || dealInfo.isEmpty()) {
                continue;
            }
            if (1 == i) {
                tencentRealtimeDealInfoPo.setStockName(dealInfo);
            } else if (2 == i) {
                tencentRealtimeDealInfoPo.setStockCode(dealInfo);
            } else if (3 == i) {
                tencentRealtimeDealInfoPo.setCurrentPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (4 == i) {
                tencentRealtimeDealInfoPo.setPreClosePrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (5 == i) {
                tencentRealtimeDealInfoPo.setOpenPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (6 == i || 29 == i || 36 == i) {
                //股数
                tencentRealtimeDealInfoPo.setDealNum(new BigDecimal(dealInfo).longValue());
            } else if (9 <= i && 28 >= i) {
                if (0 == i % 2) {
                    num = Integer.valueOf(dealInfo);
                    priceMap.put(price, num);
                    if (i == 18) {
                        tencentRealtimeDealInfoPo.setBuyPriceMap(priceMap);
                        priceMap = new LinkedHashMap<>(5);
                    }
                    if (i == 28) {
                        tencentRealtimeDealInfoPo.setSellPriceMap(priceMap);
                    }
                } else {
                    price = BigDecimalUtil.initPrice(dealInfo);
                }
            } else if (30 == i) {
                tencentRealtimeDealInfoPo.setDt(
                        DateUtil.dateStrFormatChange(dealInfo, DateUtil.TIME_FORMAT_4, DateUtil.DATE_FORMAT_1)
                );
                tencentRealtimeDealInfoPo.setTime(
                        DateUtil.dateStrFormatChange(dealInfo, DateUtil.TIME_FORMAT_4, DateUtil.TIME_FORMAT_2)
                );
            } else if (31 == i) {
                tencentRealtimeDealInfoPo.setUptickPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (32 == i) {
                //*100
                tencentRealtimeDealInfoPo.setUptickRate(BigDecimalUtil.initPrice(dealInfo));
            } else if (33 == i) {
                tencentRealtimeDealInfoPo.setHighestPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (34 == i) {
                tencentRealtimeDealInfoPo.setLowestPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (35 == i) {
                tencentRealtimeDealInfoPo.setCurrentPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (37 == i) {
                tencentRealtimeDealInfoPo.setDealMoney(BigDecimalUtil.initPrice(dealInfo));
            } else if (38 == i) {
            } else if (39 == i) {
                //*100
                tencentRealtimeDealInfoPo.setPerRate(BigDecimalUtil.initPrice(dealInfo));
            } else if (41 == i) {
                //最高价
            } else if (42 == i) {
                //最低价
            } else if (43 == i) {
                //*100
                tencentRealtimeDealInfoPo.setSurgeRate(BigDecimalUtil.initPrice(dealInfo));
            } else if (44 == i) {
                //亿
                tencentRealtimeDealInfoPo.setCircValue(BigDecimalUtil.initPrice(dealInfo));
            } else if (45 == i) {
                //亿
                tencentRealtimeDealInfoPo.setTotalValue(BigDecimalUtil.initPrice(dealInfo));
            } else if (46 == i) {
                tencentRealtimeDealInfoPo.setStockNameEn(dealInfo);
            } else if (48 == i) {
                tencentRealtimeDealInfoPo.setFiftyTwoWeekHighestPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (49 == i) {
                tencentRealtimeDealInfoPo.setFiftyTwoWeekLowestPrice(BigDecimalUtil.initPrice(dealInfo));
            } else if (59 == i) {
                //*100
                tencentRealtimeDealInfoPo.setTurnoverRate(BigDecimalUtil.initPrice(dealInfo));
            } else {
                unknownList.add(dealInfo);
            }
        }
        tencentRealtimeDealInfoPo.setUnknownKeyList(unknownList);
        return tencentRealtimeDealInfoPo;
    }
}
