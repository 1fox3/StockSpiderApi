package com.fox.spider.stock.api.sh;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.sh.SHStockInfoPo;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 沪市股票信息
 *
 * @author lusongsong
 * @date 2020/11/19 17:59
 */
@Component
public class SHStockInfoApi extends SHBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     */
    private static final String API_URL = "http://query.sse.com.cn/commonQuery.do";
    /**
     * 基本信息
     */
    private static final String SQL_ID_INFO = "COMMON_SSE_ZQPZ_GP_GPLB_C";
    /**
     * 股本信息
     */
    private static final String SQL_ID_EQUITY = "COMMON_SSE_CP_GPLB_GPGK_GBJG_C";
    /**
     * 上市日期
     */
    private static final String SQL_ID_ON_DATE = "COMMON_SSE_ZQPZ_GP_GPLB_AGSSR_C";
    /**
     * 当前链接
     */
    private static final String REFERER_URL = "http://www.sse.com.cn/assortment/stock/list/info/company/index.shtml?COMPANY_CODE=";

    /**
     * 获取当前访问链接
     *
     * @param stockCode
     * @return
     */
    private String getRefererUrl(String stockCode) {
        if (null == stockCode || stockCode.isEmpty()) {
            return REFERER_URL;
        }
        StringBuffer stringBuffer = new StringBuffer(REFERER_URL.length() + stockCode.length());
        stringBuffer.append(REFERER_URL);
        stringBuffer.append(stockCode);
        return stringBuffer.toString();
    }

    /**
     * 解析返回数据
     *
     * @param httpUtil
     * @return
     */
    private JSONObject handleHttpResponse(HttpUtil httpUtil) {
        try {
            HttpResponseDto httpResponseDto = httpUtil.request();
            String responseContent = httpResponseDto.getContent();
            if (null != responseContent && !responseContent.equals("")) {
                JSONObject baseObject = JSONObject.parseObject(responseContent);
                if (!baseObject.isEmpty() && baseObject.containsKey("result")) {
                    JSONArray resultArray = baseObject.getJSONArray("result");
                    if (null != resultArray && 1 == resultArray.size()) {
                        return resultArray.getJSONObject(0);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 获取沪市股票信息
     *
     * @param stockCode
     * @return
     */
    public SHStockInfoPo stockInfo(String stockCode) {
        if (null == stockCode || stockCode.isEmpty()) {
            return null;
        }

        String headerReferer = getRefererUrl(stockCode);
        //基本信息
        HttpUtil baseInfoHttpUtil = new HttpUtil();
        baseInfoHttpUtil.setUrl(API_URL);
        baseInfoHttpUtil.setParam("sqlId", SQL_ID_INFO);
        baseInfoHttpUtil.setParam("productid", stockCode);
        baseInfoHttpUtil.setHeader("Referer", headerReferer);
        JSONObject baseInfoObject = handleHttpResponse(baseInfoHttpUtil);
        if (null == baseInfoObject) {
            return null;
        }
        SHStockInfoPo shStockInfoPo = new SHStockInfoPo();
        shStockInfoPo.setStockMarket(StockConst.SM_SH);
        shStockInfoPo.setStockCode(stockCode);
        if (baseInfoObject.containsKey("COMPANY_ABBR")) {
            shStockInfoPo.setStockName(baseInfoObject.getString("COMPANY_ABBR"));
        }
        if (baseInfoObject.containsKey("ENGLISH_ABBR")) {
            shStockInfoPo.setStockNameEn(baseInfoObject.getString("ENGLISH_ABBR"));
        }
        if (baseInfoObject.containsKey("FULLNAME")) {
            shStockInfoPo.setStockFullName(baseInfoObject.getString("FULLNAME"));
        }
        if (baseInfoObject.containsKey("FULL_NAME_IN_ENGLISH")) {
            shStockInfoPo.setStockFullNameEn(baseInfoObject.getString("FULL_NAME_IN_ENGLISH"));
        }
        if (baseInfoObject.containsKey("LEGAL_REPRESENTATIVE")) {
            shStockInfoPo.setStockLegal(baseInfoObject.getString("LEGAL_REPRESENTATIVE").trim());
        }
        if (baseInfoObject.containsKey("COMPANY_ADDRESS")) {
            shStockInfoPo.setStockRegisterAddress(baseInfoObject.getString("COMPANY_ADDRESS"));
        }
        if (baseInfoObject.containsKey("OFFICE_ADDRESS")) {
            shStockInfoPo.setStockConnectAddress(baseInfoObject.getString("OFFICE_ADDRESS"));
        }
        if (baseInfoObject.containsKey("E_MAIL_ADDRESS")) {
            shStockInfoPo.setStockEmail(baseInfoObject.getString("E_MAIL_ADDRESS"));
        }
        if (baseInfoObject.containsKey("WWW_ADDRESS")) {
            shStockInfoPo.setStockWebsite(baseInfoObject.getString("WWW_ADDRESS"));
        }
        if (baseInfoObject.containsKey("SSE_CODE_DESC")) {
            shStockInfoPo.setStockIndustry(baseInfoObject.getString("SSE_CODE_DESC"));
        }
        if (baseInfoObject.containsKey("CSRC_CODE_DESC") && baseInfoObject.containsKey("CSRC_GREAT_CODE_DESC")) {
            shStockInfoPo.setStockCarc(baseInfoObject.getString("CSRC_CODE_DESC") + "/" + baseInfoObject.getString("CSRC_GREAT_CODE_DESC"));
        }
        if (baseInfoObject.containsKey("AREA_NAME_DESC")) {
            shStockInfoPo.setStockProvince(baseInfoObject.getString("AREA_NAME_DESC"));
        }
        //股本信息
        HttpUtil equityHttpUtil = new HttpUtil();
        equityHttpUtil.setUrl(API_URL);
        equityHttpUtil.setParam("sqlId", SQL_ID_EQUITY);
        equityHttpUtil.setParam("companyCode", stockCode);
        equityHttpUtil.setHeader("Referer", headerReferer);
        JSONObject equityInfoObject = handleHttpResponse(equityHttpUtil);
        if (null != equityInfoObject) {
            if (equityInfoObject.containsKey("DOMESTIC_SHARES")) {
                shStockInfoPo.setStockTotalEquity(equityInfoObject.getDouble("DOMESTIC_SHARES"));
            }
            if (equityInfoObject.containsKey("UNLIMITED_SHARES")) {
                shStockInfoPo.setStockCircEquity(equityInfoObject.getDouble("UNLIMITED_SHARES"));
            }
        }
        //上市日期
        HttpUtil onDateHttpUtil = new HttpUtil();
        onDateHttpUtil.setUrl(API_URL);
        onDateHttpUtil.setParam("sqlId", SQL_ID_ON_DATE);
        onDateHttpUtil.setParam("productid", stockCode);
        onDateHttpUtil.setHeader("Referer", headerReferer);
        JSONObject onDateObject = handleHttpResponse(onDateHttpUtil);
        if (null != onDateObject) {
            if (onDateObject.containsKey("LISTINGDATEA")) {
                shStockInfoPo.setStockOnDate(onDateObject.getString("LISTINGDATEA"));
            }
        }
        return shStockInfoPo;
    }
}
