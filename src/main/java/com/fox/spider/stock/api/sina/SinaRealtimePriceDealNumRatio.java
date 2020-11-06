package com.fox.spider.stock.api.sina;

import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.sina.SinaPriceDealNumRatioPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 股票实时成交价格占比
 * 不支持港股
 *
 * @author lusongsong
 * @date 2020/11/5 14:22
 */
@Component
public class SinaRealtimePriceDealNumRatio extends SinaBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 样例链接
     * https://vip.stock.finance.sina.com.cn/quotes_service/view/cn_price.php?symbol=sh603383
     */
    /**
     * 接口
     */
    private static String apiUrl = "https://vip.stock.finance.sina.com.cn/quotes_service/view/cn_price.php";

    /**
     * 获取实时成交价格占比
     *
     * @param stockVo
     * @return
     */
    public List<SinaPriceDealNumRatioPo> priceDealNumRatio(StockVo stockVo) {
        List<SinaPriceDealNumRatioPo> list = new ArrayList<>();
        try {
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(apiUrl)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            httpUtil.setParam("symbol", SinaBaseApi.sinaStockCode(stockVo));
            HttpResponseDto httpResponse = httpUtil.request();
            list = this.handleResponse(httpResponse.getContent());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return list;
    }

    /**
     * 解析返回的信息
     *
     * @param response
     * @return
     */
    private List<SinaPriceDealNumRatioPo> handleResponse(String response) {
        List<SinaPriceDealNumRatioPo> list = new ArrayList<>();
        int divStartIndex = response.indexOf("<div id=\"divListTemplate\" class=\"divList\">");
        if (0 <= divStartIndex) {
            response = response.substring(divStartIndex);
        }
        int divEndIndex = response.indexOf("</table");
        if (0 <= divEndIndex) {
            response = response.substring(0, divEndIndex);
        }
        //截取表格内容
        int bodyStartIndex = response.indexOf("<tbody");
        int bodyEndIndex = response.lastIndexOf("</tbody");
        if (bodyStartIndex < 0 || bodyEndIndex < 0) {
            return list;
        }
        response = response.substring(bodyStartIndex, bodyEndIndex);
        //匹配没一行的tb属性
        String patternStr = "<td>(.*)<\\/*?";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(response);
        SinaPriceDealNumRatioPo stockDealNumPo = null;
        int i = 0;
        while (matcher.find()) {
            if (null == stockDealNumPo) {
                stockDealNumPo = new SinaPriceDealNumRatioPo();
            }
            i++;
            String e = matcher.group(1);
            //匹配价格
            if (1 == i) {
                stockDealNumPo.setPrice(BigDecimalUtil.initPrice(Double.valueOf(e)));
            }
            //匹配成交量
            if (2 == i) {
                stockDealNumPo.setDealNum(Long.valueOf(e));
            }
            //匹配占比
            if (3 == i) {
                //去掉占比的百分号
                e = e.replace("%", "");
                stockDealNumPo.setRatio(BigDecimalUtil.initPrice(Double.valueOf(e)));
                list.add(stockDealNumPo);
                stockDealNumPo = null;
                i = 0;
            }
        }
        return list;
    }
}
