package com.fox.spider.stock.api.tencent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.tencent.TencentKLineNodeDataPo;
import com.fox.spider.stock.entity.po.tencent.TencentKLinePo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.DateUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 腾信k线图数据
 * 返回的数据会比limit多一天
 *
 * @author lusongsong
 * @date 2020/12/28 18:22
 */
@Component
public class TencentKLineApi extends TencentKLineBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * A股接口地址
     * https://proxy.finance.qq.com/ifzqgtimg/appstock/app/newfqkline/get?_var=kline_dayqfq&param=sz000002,day,,,320,qfq
     */
    private static final String A_API_URL = "https://proxy.finance.qq.com/ifzqgtimg/appstock/app/newfqkline/get";
    /**
     * 港股接口地址
     */
    private static final String HK_FQ_API_URL = "https://web.ifzq.gtimg.cn/appstock/app/hkfqkline/get";
    private static final String HK_API_URL = "https://web.ifzq.gtimg.cn/appstock/app/kline/kline";
    /**
     * 日期类型列表
     */
    private static List<Integer> DATE_TYPE_LIST = Arrays.asList(
            StockConst.DT_DAY,
            StockConst.DT_WEEK,
            StockConst.DT_MONTH
    );

    /**
     * 获取请求接口地址
     *
     * @param stockMarket
     * @return
     */
    private String getApiUrl(Integer stockMarket,Integer fqType) {
        switch (stockMarket) {
            case StockConst.SM_HK:
                switch (fqType) {
                    case StockConst.SFQ_NO:
                        return HK_API_URL;
                    default:
                        return HK_FQ_API_URL;
                }
            default:
                return A_API_URL;
        }
    }

    /**
     * 获取var参数
     *
     * @param dateType
     * @param fqType
     * @return
     */
    private String getParamVar(Integer dateType, Integer fqType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("kline_");
        stringBuilder.append(getDateTypeStr(dateType));
        stringBuilder.append(getFQTypeStr(fqType));
        return stringBuilder.toString();
    }

    /**
     * 获取日期类型字符串
     *
     * @param dateType
     * @return
     */
    private String getDateTypeStr(Integer dateType) {
        switch (dateType) {
            case StockConst.DT_WEEK:
                return "week";
            case StockConst.DT_MONTH:
                return "month";
            default:
                return "day";
        }
    }

    /**
     * 获取复权类型字符串
     *
     * @param fqType
     * @return
     */
    private String getFQTypeStr(Integer fqType) {
        switch (fqType) {
            case StockConst.SFQ_BEFORE:
                return "qfq";
            case StockConst.SFQ_AFTER:
                return "hfq";
            default:
                return "";
        }
    }

    /**
     * 获取返回数据中k线数据的key
     *
     * @param dateType
     * @param fqType
     * @return
     */
    private String getResponseKLineKey(Integer dateType, Integer fqType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getFQTypeStr(fqType));
        stringBuilder.append(getDateTypeStr(dateType));
        return stringBuilder.toString();
    }

    /**
     * 获取股票不同粒度线图
     *
     * @param stockVo
     * @param dateType
     * @param len
     * @return
     */
    public TencentKLinePo kLine(StockVo stockVo, Integer dateType, Integer fqType, Integer len) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()
                || null == dateType || dateType < 0 || !DATE_TYPE_LIST.contains(dateType) || null == len || len < 0) {
            return null;
        }
        try {
            String tencnetStockCode = TencentBaseApi.tencentStockCode(stockVo);
            Map<String, String> params = new HashMap<>(2);
            params.put("param", StringUtils.join(Arrays.asList(
                    tencnetStockCode,
                    getDateTypeStr(dateType),
                    "",
                    "",
                    len.toString(),
                    getFQTypeStr(fqType)
            ), ","));
            params.put("_var", getParamVar(dateType, fqType));
            HttpUtil httpUtil = new HttpUtil().setUrl(getApiUrl(stockVo.getStockMarket(), fqType))
                    .setParams(params)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            TencentKLinePo tencentKLinePo = this.handleResponse(
                    stockVo, dateType, fqType, httpResponse.getContent()
            );
            if (null != tencentKLinePo) {
                tencentKLinePo.setStockMarket(stockVo.getStockMarket());
                tencentKLinePo.setStockCode(stockVo.getStockCode());
                tencentKLinePo.setKLineType(dateType);
            }
            return tencentKLinePo;
        } catch (IOException e) {
            logger.error(stockVo.toString(), e);
        }
        return null;
    }

    /**
     * 解析返回数据
     *
     * @param stockVo
     * @param dateType
     * @param fqType
     * @param response
     * @return
     */
    private TencentKLinePo handleResponse(StockVo stockVo, Integer dateType, Integer fqType, String response) {
        try {
            String stockCode = TencentBaseApi.tencentStockCode(stockVo);
            JSONObject jsonObject = getResponseJSONObject(stockVo, response);
            TencentKLinePo tencentKLinePo = new TencentKLinePo();
            if (jsonObject.containsKey(RESPONSE_KEY_PRE_CLOSE_PRICE)) {
                tencentKLinePo.setPreClosePrice(
                        BigDecimalUtil.initPrice(jsonObject.getString(RESPONSE_KEY_PRE_CLOSE_PRICE))
                );
            }
            JSONObject dealInfoObject = jsonObject.containsKey(RESPONSE_KEY_QT) ?
                    jsonObject.getJSONObject(RESPONSE_KEY_QT) : null;

            if (null != dealInfoObject && dealInfoObject.containsKey(stockCode)) {
                JSONArray dealInfoArr = dealInfoObject.getJSONArray(stockCode);
                if (null != dealInfoArr) {
                    int len = dealInfoArr.size();
                    if (1 < len) {
                        tencentKLinePo.setStockName(dealInfoArr.getString(1));
                    }
                }
            }
            String kLineDataKey = getResponseKLineKey(dateType, fqType);
            JSONArray kLineDataArr = jsonObject.containsKey(kLineDataKey) ?
                    jsonObject.getJSONArray(kLineDataKey) : null;
            if (null != kLineDataArr && !kLineDataArr.isEmpty()) {
                List<TencentKLineNodeDataPo> tencentKLineNodeDataPoList = new ArrayList<>();
                for (int dateIndex = 0; dateIndex < kLineDataArr.size(); dateIndex++) {
                    JSONArray oneNodeArr = kLineDataArr.getJSONArray(dateIndex);
                    if (null == oneNodeArr || oneNodeArr.isEmpty()) {
                        continue;
                    }
                    TencentKLineNodeDataPo tencentKLineNodeDataPo = new TencentKLineNodeDataPo();
                    for (int i = 0; i < oneNodeArr.size(); i++) {
                        if (0 == i) {
                            tencentKLineNodeDataPo.setDt(oneNodeArr.getString(i));
                        }
                        if (1 == i) {
                            tencentKLineNodeDataPo.setOpenPrice(
                                    BigDecimalUtil.initPrice(oneNodeArr.getString(i))
                            );
                        }
                        if (2 == i) {
                            tencentKLineNodeDataPo.setClosePrice(
                                    BigDecimalUtil.initPrice(oneNodeArr.getString(i))
                            );
                        }
                        if (3 == i) {
                            tencentKLineNodeDataPo.setHighestPrice(
                                    BigDecimalUtil.initPrice(oneNodeArr.getString(i))
                            );
                        }
                        if (4 == i) {
                            tencentKLineNodeDataPo.setLowestPrice(
                                    BigDecimalUtil.initPrice(oneNodeArr.getString(i))
                            );
                        }
                        if (5 == i) {
                            tencentKLineNodeDataPo.setDealNum(
                                    handleDealNum(stockVo, oneNodeArr.getString(i))
                            );
                        }
                        if (8 == i) {
                            //万
                            tencentKLineNodeDataPo.setDealMoney(
                                    BigDecimalUtil.initPrice(oneNodeArr.getString(i))
                            );
                        }
                    }
                    tencentKLineNodeDataPoList.add(tencentKLineNodeDataPo);
                }
                tencentKLinePo.setNodeCount(tencentKLineNodeDataPoList.size());
                tencentKLinePo.setKlineData(tencentKLineNodeDataPoList);
            }
            return tencentKLinePo;
        } catch (Exception e) {
            logger.error(response, e);
        }
        return null;
    }
}
