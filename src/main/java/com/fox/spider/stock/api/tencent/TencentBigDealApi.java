package com.fox.spider.stock.api.tencent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.tencent.TencentBigDealPo;
import com.fox.spider.stock.entity.vo.PageVo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 腾讯大单交易信息
 *
 * @author lusongsong
 * @date 2020/12/31 15:01
 */
@Component
public class TencentBigDealApi extends TencentBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 接口地址
     * https://stock.finance.qq.com/sstock/list/view/dadan.php?t=js&c=sz000001&max=80&p=1&opt=10&o=0
     */
    private static final String API_URL = "https://stock.finance.qq.com/sstock/list/view/dadan.php";

    /**
     * 大单交易信息分割符
     */
    private static String BIG_DEAL_SPLIT_STR = "^";
    /**
     * 大单交易详情信息分割符
     */
    private static String BIG_DEAL_INFO_SPLIT_STR = "~";
    /**
     * 大单筛选条件
     * 成交量
     * 1:>=100手
     * 2:>=200手
     * 3:>=300手
     * 4:>=400手
     * 5:>=500手
     * 6:>=800手
     * 7:>=1000手
     * 8:>=1500手
     * 9:>=2000手
     * 成交金额
     * 10:>=100万
     * 11:>=200万
     * 12:>=500万
     * 13:>=1000万
     */
    public static List<List<Integer>> FILTER_OPT_LIST = Arrays.asList(
            Arrays.asList(1, 1, 100),
            Arrays.asList(2, 1, 200),
            Arrays.asList(3, 1, 300),
            Arrays.asList(4, 1, 400),
            Arrays.asList(5, 1, 500),
            Arrays.asList(6, 1, 800),
            Arrays.asList(7, 1, 1000),
            Arrays.asList(8, 1, 1500),
            Arrays.asList(9, 1, 2000),
            Arrays.asList(10, 2, 100),
            Arrays.asList(11, 2, 200),
            Arrays.asList(12, 2, 500),
            Arrays.asList(13, 2, 1000)
    );

    /**
     * 排序规则
     * 0：时间降序
     * 1：时间升序
     * 2：成交价降序
     * 3：成交价升序
     * 4：成交额降序
     * 5：成交额升序
     * 6：成交量降序
     * 7：成交量升序
     * 8：性质降序
     * 9：性质升序
     */
    public static List<Integer> SORT_TYPE_LIST = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

    /**
     * 获取大单交易信息
     *
     * @param stockVo
     * @return
     */
    public List<TencentBigDealPo> bigDeal(StockVo stockVo, Integer filterOpt, Integer sortType, PageVo pageVo) {
        if (null == stockVo || null == stockVo.getStockMarket() || null == stockVo.getStockCode()) {
            return null;
        }
        try {
            String tencnetStockCode = TencentBaseApi.tencentStockCode(stockVo);
            Map<String, String> params = new HashMap<>(6);
            params.put("t", "js");
            params.put("c", tencnetStockCode);
            params.put("max", pageVo.getPageSize().toString());
            params.put("p", pageVo.getPageNum().toString());
            params.put("opt", filterOpt.toString());
            params.put("o", sortType.toString());
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL)
                    .setParams(params)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            HttpResponseDto httpResponse = httpUtil.request();
            List<TencentBigDealPo> tencentBigDealPoList = this.handleResponse(
                    stockVo, httpResponse.getContent()
            );
            return tencentBigDealPoList;
        } catch (IOException e) {
            logger.error(stockVo.toString(), e);
        }
        return null;
    }

    /**
     * 解析返回数据
     *
     * @param stockVo
     * @param response
     * @return
     */
    private List<TencentBigDealPo> handleResponse(StockVo stockVo, String response) {
        if (null == stockVo || null == response || response.isEmpty()) {
            return null;
        }
        try {
            int infoStartIndex = response.indexOf("[");
            int infoEndIndex = response.lastIndexOf("]");
            if (-1 == infoStartIndex || -1 == infoEndIndex) {
                return null;
            }
            response = response.substring(infoStartIndex, infoEndIndex + 1);
            response = response.replace("'", "\"");
            JSONArray jsonArray = JSONArray.parseArray(response);
            if (null == jsonArray || jsonArray.isEmpty()) {
                return null;
            }
            response = jsonArray.getString(1);
            if (null == response || response.isEmpty()) {
                return null;
            }

            String[] bigDealArr = StringUtils.split(response, BIG_DEAL_SPLIT_STR);
            List<TencentBigDealPo> tencentBigDealPoList = new ArrayList<>(bigDealArr.length);
            for (int i = 0; i < bigDealArr.length; i++) {
                String singleBigDealStr = bigDealArr[i];
                if (null == singleBigDealStr || singleBigDealStr.isEmpty()) {
                    continue;
                }
                TencentBigDealPo tencentBigDealPo =
                        handleSingleBigDeal(stockVo, singleBigDealStr);
                if (null != tencentBigDealPo) {
                    tencentBigDealPoList.add(tencentBigDealPo);
                }
            }
            return tencentBigDealPoList;
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }

    /**
     * 处理单条大单交易详情
     *
     * @param stockVo
     * @param singleBigDealStr
     * @return
     */
    private TencentBigDealPo handleSingleBigDeal(StockVo stockVo, String singleBigDealStr) {
        if (null == singleBigDealStr || singleBigDealStr.isEmpty()) {
            return null;
        }
        String[] bigDealInfoArr = StringUtils.split(singleBigDealStr, BIG_DEAL_INFO_SPLIT_STR);
        TencentBigDealPo tencentBigDealPo = new TencentBigDealPo();
        for (int i = 0; i < bigDealInfoArr.length; i++) {
            if (1 == i) {
                tencentBigDealPo.setTime(bigDealInfoArr[i]);
            }
            if (2 == i) {
                tencentBigDealPo.setPrice(BigDecimalUtil.initPrice(bigDealInfoArr[i]));
            }
            if (3 == i) {
                tencentBigDealPo.setDealNum(handleDealNum(stockVo, bigDealInfoArr[i]));
            }
            if (4 == i) {
                tencentBigDealPo.setDealMoney(BigDecimalUtil.initPrice(bigDealInfoArr[i]));
            }
            if (5 == i) {
                tencentBigDealPo.setDealType(getDealType(bigDealInfoArr[i]));
            }
        }
        return tencentBigDealPo;
    }
}
