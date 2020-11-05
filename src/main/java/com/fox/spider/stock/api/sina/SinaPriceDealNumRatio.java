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
 * 股票成交价格占比
 * 只能情请求截止至上个交易日的数据，不能高频次访问，会被拦截
 *
 * @author lusongsong
 * @date 2020/11/5 14:08
 */
@Component
public class SinaPriceDealNumRatio extends SinaBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 样例链接
     * http://market.finance.sina.com.cn/iframe/pricehis.php?symbol=sh603383&startdate=2019-12-01&enddate=2019-12-13
     */
    /**
     * 接口
     */
    private static String apiUrl = "http://market.finance.sina.com.cn/iframe/pricehis.php";

    /**
     * 股票成交价格占比
     * 不支持港股
     *
     * @param stockVo
     * @param startDate
     * @param endDate
     * @return
     */
    public List<SinaPriceDealNumRatioPo> priceDealNumRatio(StockVo stockVo, String startDate, String endDate) {
        List<SinaPriceDealNumRatioPo> list = new ArrayList<>();
        try {
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.setUrl(apiUrl)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            httpUtil.setParam("symbol", SinaBaseApi.sinaStockCode(stockVo));
            httpUtil.setParam("startdate", startDate);
            httpUtil.setParam("enddate", endDate);
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
        //截取表格内容
        int bodyStartIndex = response.indexOf("<tbody");
        int bodyEndIndex = response.lastIndexOf("</tbody");
        if (bodyStartIndex < 0 || bodyEndIndex < 0) {
            return list;
        }
        response = response.substring(bodyStartIndex, bodyEndIndex);
        //匹配没一行的tb属性
        String patternStr = "<td>(.*)<\\/td>*?";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(response);
        SinaPriceDealNumRatioPo sinaPriceDealNumRatioPo = null;
        int i = 0;
        while (matcher.find()) {
            if (null == sinaPriceDealNumRatioPo) {
                sinaPriceDealNumRatioPo = new SinaPriceDealNumRatioPo();
            }
            i++;
            String e = matcher.group(1);
            //匹配价格
            if (1 == i) {
                sinaPriceDealNumRatioPo.setPrice(BigDecimalUtil.initPrice(Double.valueOf(e)));
            }
            //匹配成交量
            if (2 == i) {
                sinaPriceDealNumRatioPo.setDealNum(Long.valueOf(e));
            }
            //匹配占比
            if (3 == i) {
                //去掉占比的百分号
                e = e.replace("%", "");
                sinaPriceDealNumRatioPo.setRatio(BigDecimalUtil.initPrice(Double.valueOf(e)));
                list.add(sinaPriceDealNumRatioPo);
                sinaPriceDealNumRatioPo = null;
                i = 0;
            }
        }
        return list;
    }
}
