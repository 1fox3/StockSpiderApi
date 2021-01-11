package com.fox.spider.stock.api.sina;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import com.fox.spider.stock.entity.po.sina.SinaBigDealPo;
import com.fox.spider.stock.entity.po.sina.SinaBigDealSumPo;
import com.fox.spider.stock.entity.vo.PageVo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.entity.vo.sina.SinaBigDealFilterVo;
import com.fox.spider.stock.entity.vo.sina.SinaBigDealSortVo;
import com.fox.spider.stock.util.BigDecimalUtil;
import com.fox.spider.stock.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 新浪大单交易接口
 *
 * @author lusongsong
 * @date 2021/1/8 17:36
 */
@Component
public class SinaBigDealApi extends SinaBaseApi {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 交易汇总接口地址
     * https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_Bill.GetBillSum?symbol=sh603383&num=60&sort=ticktime&asc=0&volume=0&amount=500000&type=0&day=2021-01-08
     */
    private static final String API_URL_SUM =
            "https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_Bill.GetBillSum";
    /**
     * 大单交易页数接口
     * https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_Bill.GetBillListCount?symbol=sh603383&num=60&page=1&sort=ticktime&asc=0&volume=0&amount=500000&type=0&day=2021-01-08
     */
    private static final String API_URL_LIST_COUNT =
            "https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_Bill.GetBillListCount";
    /**
     * 大单交易列表接口
     * https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_Bill.GetBillList?symbol=sh603383&num=60&page=1&sort=ticktime&asc=0&volume=0&amount=500000&type=0&day=2021-01-08
     */
    private static final String API_URL_LIST =
            "https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_Bill.GetBillList";

    /**
     * 成交量大单定义
     */
    public static final List<Integer> DEAL_NUM_LIST = Arrays.asList(
            40000,
            50000,
            60000,
            70000,
            80000,
            90000,
            100000,
            200000,
            500000,
            1000000
    );
    /**
     * 成交金额大单定义
     */
    public static final List<Integer> DEAL_MONEY_LIST = Arrays.asList(
            500000,
            1000000,
            2000000,
            5000000,
            10000000
    );
    /**
     * 上一交易日平均每笔成交量倍数大单定义
     */
    public static final Map<Integer, Integer> TIMES_MAP = new LinkedHashMap<Integer, Integer>() {{
        put(5, 1);
        put(10, 2);
        put(20, 3);
        put(50, 4);
        put(100, 5);
    }};
    /**
     * 筛选条件-成交量
     */
    public static final String FILTER_DEAL_NUM = "volume";
    /**
     * 筛选条件-成交金额
     */
    public static final String FILTER_DEAL_MONEY = "amount";
    /**
     * 筛选条件-上一交易日平均每笔成交量倍数大单定义
     */
    public static final String FILTER_TYPE = "type";
    /**
     * 排序属性
     */
    public static final String SORT_COLUMN = "sort";
    /**
     * 排序规则(是否为升序)
     */
    public static final String SORT_ASC = "asc";
    /**
     * 大单交易排序属性列表
     */
    public static final List<String> COLUMN_LIST = Arrays.asList(
            //成交时间
            "ticktime",
            //成交价
            "price",
            //成交量
            "volume",
            //交易性质
            "kind"
    );

    /**
     * 获取交易类型
     *
     * @param text
     * @return
     */
    private int getDealType(String text) {
        switch (text) {
            case "U":
                return StockConst.DEAL_BUY;
            case "D":
                return StockConst.DEAL_SELL;
            case "E":
                return StockConst.DEAL_FLAT;
            default:
                return StockConst.DEAL_UNKNOWN;
        }
    }

    /**
     * 获取筛选条件参数
     *
     * @param sinaBigDealFilterVo
     * @return
     */
    private Map<String, Object> getFilterParamMap(SinaBigDealFilterVo sinaBigDealFilterVo) {
        Integer dealNum = null, dealMoney = null, times = null;
        if (null != sinaBigDealFilterVo) {
            dealNum = sinaBigDealFilterVo.getDealNum();
            dealMoney = sinaBigDealFilterVo.getDealMoney();
            times = sinaBigDealFilterVo.getTimes();
        }
        Map<String, Object> filterParamMap = new HashMap<>(3);
        dealNum = DEAL_NUM_LIST.contains(dealNum) ? dealNum : 0;
        dealMoney = DEAL_MONEY_LIST.contains(dealMoney) ? dealMoney : 0;
        times = TIMES_MAP.containsKey(times) ? TIMES_MAP.get(times) : 0;
        filterParamMap.put(FILTER_DEAL_NUM, dealNum);
        filterParamMap.put(FILTER_DEAL_MONEY, dealMoney);
        filterParamMap.put(FILTER_TYPE, times);
        return filterParamMap;
    }

    /**
     * 获取排序条件参数
     *
     * @param sinaBigDealSortVo
     * @return
     */
    private Map<String, Object> getSortParamMap(SinaBigDealSortVo sinaBigDealSortVo) {
        String column = null;
        Integer asc = 0;
        if (null != sinaBigDealSortVo) {
            column = sinaBigDealSortVo.getColumn();
            asc = sinaBigDealSortVo.getAsc();
        }
        column = COLUMN_LIST.contains(column) ? column : COLUMN_LIST.get(0);
        asc = asc.equals(1) ? asc : 0;
        Map<String, Object> sortParamMap = new HashMap<>(2);
        sortParamMap.put(SORT_COLUMN, column);
        sortParamMap.put(SORT_ASC, asc);
        return sortParamMap;
    }

    /**
     * 获取大单交易总页数
     *
     * @param stockVo
     * @return
     */
    public Integer bigDealListCount(
            StockVo stockVo, SinaBigDealFilterVo sinaBigDealFilterVo, String dt
    ) {
        if (!StockVo.verify(stockVo)) {
            return null;
        }
        try {
            String sinaStockCode = sinaStockCode(stockVo);
            Map<String, Object> filterParams = getFilterParamMap(sinaBigDealFilterVo);
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL_LIST_COUNT)
                    .setParam("symbol", sinaStockCode)
                    .setParams(filterParams)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            if (null != dt && !dt.isEmpty()) {
                httpUtil.setParam("day", dt);
            }
            HttpResponseDto httpResponse = httpUtil.request();
            String count = (String) JSON.parse(httpResponse.getContent());
            return Integer.valueOf(count);
        } catch (Exception e) {
            logger.error(stockVo.toString(), e);
        }
        return null;
    }

    /**
     * 大单汇总
     *
     * @param stockVo
     * @return
     */
    public SinaBigDealSumPo bigDealSum(
            StockVo stockVo,
            SinaBigDealFilterVo sinaBigDealFilterVo,
            String dt
    ) {
        if (!StockVo.verify(stockVo)) {
            return null;
        }
        try {
            String sinaStockCode = sinaStockCode(stockVo);
            Map<String, Object> filterParams = getFilterParamMap(sinaBigDealFilterVo);
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL_SUM)
                    .setParam("symbol", sinaStockCode)
                    .setParams(filterParams)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            if (null != dt && !dt.isEmpty()) {
                httpUtil.setParam("day", dt);
            }
            HttpResponseDto httpResponse = httpUtil.request();
            return handleSumResponse(stockVo, httpResponse.getContent());
        } catch (Exception e) {
            logger.error(stockVo.toString(), e);
        }
        return null;
    }

    /**
     * 处理大单汇总返回
     *
     * @param stockVo
     * @param response
     * @return
     */
    private SinaBigDealSumPo handleSumResponse(StockVo stockVo, String response) {
        if (!StockVo.verify(stockVo) || null == response || response.isEmpty()) {
            return null;
        }
        try {
            JSONObject jsonObject = null;
            JSONArray jsonArray = JSON.parseArray(response);
            if (null != jsonArray) {
                jsonObject = jsonArray.getJSONObject(0);
            }
            if (null != jsonObject) {
                SinaBigDealSumPo sinaBigDealSumPo = new SinaBigDealSumPo();
                sinaBigDealSumPo.setStockMarket(stockVo.getStockMarket());
                sinaBigDealSumPo.setStockCode(stockVo.getStockCode());
                if (jsonObject.containsKey("name")) {
                    sinaBigDealSumPo.setStockName(jsonObject.getString("name"));
                }
                if (jsonObject.containsKey("opendate")) {
                    sinaBigDealSumPo.setDt(
                            jsonObject.getString("opendate")
                    );
                }
                if (jsonObject.containsKey("avgprice")) {
                    sinaBigDealSumPo.setAvgPrice(
                            BigDecimalUtil.initPrice(jsonObject.getString("avgprice"))
                    );
                }
                if (jsonObject.containsKey("stockvol")) {
                    sinaBigDealSumPo.setDealNum(
                            BigDecimalUtil.initLong(jsonObject.getString("stockvol"))
                    );
                }
                if (jsonObject.containsKey("stockamt")) {
                    sinaBigDealSumPo.setDealMoney(
                            BigDecimalUtil.initPrice(jsonObject.getString("stockamt"))
                    );
                }
                if (jsonObject.containsKey("totalvol")) {
                    sinaBigDealSumPo.setBigDealNum(
                            BigDecimalUtil.initLong(jsonObject.getString("totalvol"))
                    );
                }
                if (jsonObject.containsKey("totalamt")) {
                    sinaBigDealSumPo.setBigDealMoney(
                            BigDecimalUtil.initPrice(jsonObject.getString("totalamt"))
                    );
                }
                if (jsonObject.containsKey("kuvolume")) {
                    sinaBigDealSumPo.setBigBuyDealNum(
                            BigDecimalUtil.initLong(jsonObject.getString("kuvolume"))
                    );
                }
                if (jsonObject.containsKey("kuamount")) {
                    sinaBigDealSumPo.setBigBuyDealMoney(
                            BigDecimalUtil.initPrice(jsonObject.getString("kuamount"))
                    );
                }
                if (jsonObject.containsKey("kdvolume")) {
                    sinaBigDealSumPo.setBigSellDealNum(
                            BigDecimalUtil.initLong(jsonObject.getString("kdvolume"))
                    );
                }
                if (jsonObject.containsKey("kdamount")) {
                    sinaBigDealSumPo.setBigSellDealMoney(
                            BigDecimalUtil.initPrice(jsonObject.getString("kdamount"))
                    );
                }
                if (jsonObject.containsKey("kevolume")) {
                    sinaBigDealSumPo.setBigFlatDealNum(
                            BigDecimalUtil.initLong(jsonObject.getString("kevolume"))
                    );
                }
                if (jsonObject.containsKey("keamount")) {
                    sinaBigDealSumPo.setBigFlatDealMoney(
                            BigDecimalUtil.initPrice(jsonObject.getString("keamount"))
                    );
                }
                if (jsonObject.containsKey("totalvolpct")) {
                    sinaBigDealSumPo.setBigDealNumRatio(
                            BigDecimalUtil.initRate(
                                    jsonObject.getString("totalvolpct"), BigDecimalUtil.RATE_MULTIPLY_100
                            )
                    );
                }
                if (jsonObject.containsKey("totalamtpct")) {
                    sinaBigDealSumPo.setBigDealMoneyRatio(
                            BigDecimalUtil.initRate(
                                    jsonObject.getString("totalamtpct"),
                                    BigDecimalUtil.RATE_MULTIPLY_100
                            )
                    );
                }
                return sinaBigDealSumPo;
            }
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }

    /**
     * 获取大单交易列表
     *
     * @param stockVo
     * @param sinaBigDealFilterVo
     * @param pageVo
     * @param dt
     * @return
     */
    public List<SinaBigDealPo> bigDealList(
            StockVo stockVo,
            SinaBigDealFilterVo sinaBigDealFilterVo,
            SinaBigDealSortVo sinaBigDealSortVo,
            PageVo pageVo,
            String dt) {
        if (!StockVo.verify(stockVo)) {
            return null;
        }
        try {
            pageVo = null == pageVo ? new PageVo(1, 20) : pageVo;
            String sinaStockCode = sinaStockCode(stockVo);
            Map<String, Object> filterParams = getFilterParamMap(sinaBigDealFilterVo);
            Map<String, Object> sortParams = getSortParamMap(sinaBigDealSortVo);
            HttpUtil httpUtil = new HttpUtil().setUrl(API_URL_LIST)
                    .setParam("symbol", sinaStockCode)
                    .setParam("page", pageVo.getPageNum())
                    .setParam("num", pageVo.getPageSize())
                    .setParams(filterParams)
                    .setParams(sortParams)
                    .setHeader("referer", API_URL_LIST)
                    .setOriCharset(HttpUtil.CHARSET_GBK)
                    .setErrorOriCharset(HttpUtil.CHARSET_UTF8);
            if (null != dt && !dt.isEmpty()) {
                httpUtil.setParam("day", dt);
            }
            HttpResponseDto httpResponse = httpUtil.request();
            return handleListResponse(stockVo, httpResponse.getContent());
        } catch (Exception e) {
            logger.error(stockVo.toString(), e);
        }
        return null;
    }

    /**
     * 解析大单交易列表返回
     *
     * @param stockVo
     * @param response
     * @return
     */
    private List<SinaBigDealPo> handleListResponse(StockVo stockVo, String response) {
        if (!StockVo.verify(stockVo) || null == response || response.isEmpty()) {
            return null;
        }
        try {
            JSONArray jsonArray = JSON.parseArray(response);
            if (null != jsonArray) {
                List<SinaBigDealPo> sinaBigDealPoList = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (null == jsonObject || jsonObject.isEmpty()) {
                        continue;
                    }
                    SinaBigDealPo sinaBigDealPo = new SinaBigDealPo();
                    sinaBigDealPo.setStockMarket(stockVo.getStockMarket());
                    sinaBigDealPo.setStockCode(stockVo.getStockCode());
                    if (jsonObject.containsKey("name")) {
                        sinaBigDealPo.setStockName(jsonObject.getString("name"));
                    }
                    if (jsonObject.containsKey("ticktime")) {
                        sinaBigDealPo.setTime(jsonObject.getString("ticktime"));
                    }
                    if (jsonObject.containsKey("price")) {
                        sinaBigDealPo.setPrice(BigDecimalUtil.initPrice(jsonObject.getString("price")));
                    }
                    if (jsonObject.containsKey("prev_price")) {
                        sinaBigDealPo.setPrePrice(BigDecimalUtil.initPrice(jsonObject.getString("prev_price")));
                    }
                    if (jsonObject.containsKey("volume")) {
                        sinaBigDealPo.setDealNum(BigDecimalUtil.initLong(jsonObject.getString("volume")));
                    }
                    if (jsonObject.containsKey("name")) {
                        sinaBigDealPo.setStockName(jsonObject.getString("name"));
                    }
                    if (jsonObject.containsKey("kind")) {
                        sinaBigDealPo.setDealType(getDealType(jsonObject.getString("kind")));
                    }
                    sinaBigDealPoList.add(sinaBigDealPo);
                }
                return sinaBigDealPoList;
            }
        } catch (JSONException e) {
            logger.error(response, e);
        }
        return null;
    }
}
