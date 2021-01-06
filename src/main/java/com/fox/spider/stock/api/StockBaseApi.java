package com.fox.spider.stock.api;

/**
 * 股票接口基类
 *
 * @author lusongsong
 * @date 2021/1/4 16:41
 */
public class StockBaseApi {
    /**
     * json字符传的起始字符
     */
    public static final char JSON_OBJECT_START_CHAR = '{';
    /**
     * json字符传的结束字符
     */
    public static final char JSON_OBJECT_END_CHAR = '}';

    /**
     * 去掉json对象字符串2边多余字符
     *
     * @param jsonStr
     * @return
     */
    public static String trimJsonObject(String jsonStr) {
        if (null == jsonStr || jsonStr.isEmpty()) {
            return null;
        }
        int startPos = jsonStr.indexOf(JSON_OBJECT_START_CHAR);
        int endPos = jsonStr.lastIndexOf(JSON_OBJECT_END_CHAR);
        if (-1 != startPos && -1 != endPos) {
            return jsonStr.substring(startPos, endPos + 1);
        }
        return null;
    }
}
