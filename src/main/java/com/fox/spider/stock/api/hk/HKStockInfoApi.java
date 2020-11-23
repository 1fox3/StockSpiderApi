package com.fox.spider.stock.api.hk;

import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.hk.HKStockInfoPo;
import com.fox.spider.stock.util.HttpUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 港股信息接口类
 *
 * @author lusongsong
 * @date 2020/11/20 14:55
 */
@Component
public class HKStockInfoApi extends HKBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 股票信息接口
     */
    private static final String INFO_API = "https://www1.hkex.com.hk/hkexwidget/data/getequityquote";
    /**
     * token接口
     */
    private static final String TOKEN_API = "https://sc.hkex.com.hk/TuniS/www.hkex.com.hk/Market-Data/Securities-Prices/Equities/Equities-Quote?sym=700&sc_lang=zh-cn";
    /**
     * 港股接口请求凭证正则表达式
     */
    private static final Pattern HK_TOKEN_PATTERN = Pattern.compile("^return \"(.*)\";$");

    /**
     * 获取接口请求凭证
     *
     * @return
     */
    public String apiToken() {
        try {
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(TOKEN_API);
            HttpResponseDto httpResponse = httpUtil.request();
            String[] strings = httpResponse.getContent().split("\n");
            for (String string : strings) {
                string = string.trim();
                // 现在创建 matcher 对象
                Matcher matcher = HK_TOKEN_PATTERN.matcher(string);
                if (matcher.find() && !matcher.group(1).equals("chn")) {
                    return matcher.group(1);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 获取香港证券交易所股票信息
     * 股票列表页面:https://sc.hkex.com.hk/TuniS/www.hkex.com.hk/Market-Data/Futures-and-Options-Prices/Single-Stock/All?sc_lang=zh-CN#&sttype=all
     * 详细信息页面:https://sc.hkex.com.hk/TuniS/www.hkex.com.hk/Market-Data/Securities-Prices/Equities/Equities-Quote?sym=700&sc_lang=zh-cn
     * 数据请求链接:https://www1.hkex.com.hk/hkexwidget/data/getequityquote?sym=700&token=evLtsLsBNAUVTPxtGqVeGze1PKjLBaJY%2bA5L7HCZx%2fI2lFgRJe68dzfQEagfRsdt&lang=chn&qid=1601974258969&callback=jQuery351036336353960645607_1601974242276
     *
     * @param stockCode
     * @return
     */
    public HKStockInfoPo stockInfo(String token, String stockCode) {
        if (null == token || token.isEmpty() || null == stockCode || stockCode.isEmpty()) {
            return null;
        }

        try {
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(INFO_API);
            httpUtil.setParam("sym", Integer.valueOf(stockCode).toString());
            httpUtil.setParam("token", token);
            httpUtil.setParam("lang", "chn");
            httpUtil.setParam("qid", "1601974258969");
            httpUtil.setParam("callback", "jQuery351036336353960645607_1601974242276");
            HttpResponseDto httpResponse = httpUtil.request();
            String responseContent = httpResponse.getContent();
            int startPos = responseContent.indexOf('(');
            int endPos = responseContent.lastIndexOf(')');
            responseContent = responseContent.substring(startPos + 1, endPos);
            if (null != responseContent && !responseContent.equals("")) {
                JSONObject baseObject = JSONObject.fromObject(responseContent);
                if (!baseObject.isNullObject() && baseObject.containsKey("data")) {
                    JSONObject dataObject = baseObject.getJSONObject("data");
                    if (!dataObject.isNullObject() && dataObject.containsKey("quote")) {
                        dataObject = dataObject.getJSONObject("quote");
                    }
                    if (null != dataObject) {
                        HKStockInfoPo hkStockInfoPo = new HKStockInfoPo();
                        hkStockInfoPo.setStockCode(stockCode);
                        if (dataObject.containsKey("nm_s")) {
                            hkStockInfoPo.setStockName(dataObject.getString("nm_s"));
                        }
                        if (dataObject.containsKey("chairman")) {
                            hkStockInfoPo.setStockLegal(dataObject.getString("chairman"));
                        }
                        if (dataObject.containsKey("amt_os") && !dataObject.getString("amt_os").isEmpty()) {
                            hkStockInfoPo.setStockTotalEquity(
                                    Double.valueOf(
                                            dataObject.getString("amt_os").replace(",", "")
                                    ) / 10000
                            );
                        }
                        if (dataObject.containsKey("issuer_name")) {
                            hkStockInfoPo.setStockFullName(dataObject.getString("issuer_name"));
                        }
                        if (dataObject.containsKey("incorpin")) {
                            hkStockInfoPo.setStockRegisterAddress(dataObject.getString("incorpin"));
                        }
                        if (dataObject.containsKey("office_address")) {
                            hkStockInfoPo.setStockConnectAddress(dataObject.getString("office_address").replace("<br/>", ""));
                        }
                        if (dataObject.containsKey("hsic_ind_classification")) {
                            hkStockInfoPo.setStockCarc(dataObject.getString("hsic_ind_classification").replace(" ", ""));
                        }
                        if (dataObject.containsKey("hsic_sub_sector_classification")) {
                            hkStockInfoPo.setStockIndustry(dataObject.getString("hsic_sub_sector_classification").replace(" ", ""));
                        }
                        if (dataObject.containsKey("listing_date")) {
                            hkStockInfoPo.setStockOnDate(
                                    dataObject.getString("listing_date")
                                            .replace("年", "-")
                                            .replace("月", "-")
                                            .replace("日", "")
                            );
                        }
                        return hkStockInfoPo;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(stockCode);
            logger.error(e.getMessage());
        }
        return null;
    }
}
