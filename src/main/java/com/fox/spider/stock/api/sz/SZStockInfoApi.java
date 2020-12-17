package com.fox.spider.stock.api.sz;

import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.sz.SZStockInfoPo;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 深市股票信息接口类
 *
 * @author lusongsong
 * @date 2020/11/20 14:29
 */
@Component
public class SZStockInfoApi extends SZBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     */
    private static final String API_URL = "http://www.szse.cn/api/report/index/companyGeneralization";
    /**
     * 股票类型
     */
    private static final List<String> TYPE_LIST = Arrays.asList("a", "b");

    /**
     * 获取深市股票信息
     *
     * @param stockCode
     * @return
     */
    public SZStockInfoPo stockInfo(String stockCode) {
        if (null == stockCode || stockCode.isEmpty()) {
            return null;
        }

        try {
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(API_URL);
            httpUtil.setParam("secCode", stockCode);
            HttpResponseDto httpResponse = httpUtil.request();
            String responseContent = httpResponse.getContent();
            if (null != responseContent && !responseContent.equals("")) {
                JSONObject baseObject = JSONObject.parseObject(responseContent);
                if (!baseObject.isEmpty() && baseObject.containsKey("data")) {
                    JSONObject dataObject = baseObject.getJSONObject("data");
                    if (null != dataObject) {
                        SZStockInfoPo szStockInfoPo = new SZStockInfoPo();
                        szStockInfoPo.setStockCode(stockCode);
                        String stockCodeKey;
                        String typeStr = TYPE_LIST.get(0);
                        for (String type : TYPE_LIST) {
                            stockCodeKey = type + "gdm";
                            if (dataObject.containsKey(stockCodeKey) && stockCode.equals(dataObject.getString(stockCodeKey))) {
                                typeStr = type;
                                break;
                            }
                        }

                        if (dataObject.containsKey(typeStr + "gjc")) {
                            szStockInfoPo.setStockName(dataObject.getString(typeStr + "gjc"));
                        }
                        if (dataObject.containsKey(typeStr + "gdm")) {
                            szStockInfoPo.setStockCode(dataObject.getString(typeStr + "gdm"));
                        }
                        if (dataObject.containsKey(typeStr + "gzgb")) {
                            szStockInfoPo.setStockTotalEquity(new BigDecimal(dataObject.getString(typeStr + "gzgb").replace(",", "")));
                        }
                        if (dataObject.containsKey(typeStr + "gltgb")) {
                            szStockInfoPo.setStockCircEquity(new BigDecimal(dataObject.getString(typeStr + "gltgb").replace(",", "")));
                        }
                        if (dataObject.containsKey(typeStr + "gssrq")) {
                            szStockInfoPo.setStockOnDate(dataObject.getString(typeStr + "gssrq"));
                        }
                        if (dataObject.containsKey("gsqc")) {
                            szStockInfoPo.setStockFullName(dataObject.getString("gsqc"));
                        }
                        if (dataObject.containsKey("ywqc")) {
                            szStockInfoPo.setStockFullNameEn(dataObject.getString("ywqc"));
                        }
                        if (dataObject.containsKey("zcdz")) {
                            szStockInfoPo.setStockRegisterAddress(dataObject.getString("zcdz"));
                        }
                        if (dataObject.containsKey("http")) {
                            szStockInfoPo.setStockWebsite(dataObject.getString("http"));
                        }
                        if (dataObject.containsKey("dldq")) {
                            szStockInfoPo.setStockArea(dataObject.getString("dldq"));
                        }
                        if (dataObject.containsKey("sheng")) {
                            szStockInfoPo.setStockProvince(dataObject.getString("sheng"));
                        }
                        if (dataObject.containsKey("shi")) {
                            szStockInfoPo.setStockCity(dataObject.getString("shi"));
                        }
                        if (dataObject.containsKey("sshymc")) {
                            szStockInfoPo.setStockIndustry(dataObject.getString("sshymc"));
                        }
                        return szStockInfoPo;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
