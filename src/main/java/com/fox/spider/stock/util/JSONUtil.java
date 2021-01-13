package com.fox.spider.stock.util;

import org.apache.commons.lang3.StringUtils;

/**
 * JSON工具类
 *
 * @author lusongsong
 * @date 2021/1/13 10:57
 */
public class JSONUtil {
    /**
     * JSON对象的起始字符
     */
    public static final char JSON_OBJECT_START_CHAR = '{';
    /**
     * JSON对象的结束字符
     */
    public static final char JSON_OBJECT_END_CHAR = '}';

    /**
     * 去除JSON对象字符串2边的多余字符
     *
     * @param jsonStr
     * @return
     */
    public static String objectStrTrim(String jsonStr) {
        if (!StringUtils.isEmpty(jsonStr)) {
            int startPos = jsonStr.indexOf(JSON_OBJECT_START_CHAR);
            int endPos = jsonStr.lastIndexOf(JSON_OBJECT_END_CHAR);
            if (-1 != startPos && -1 != endPos && startPos < endPos) {
                return jsonStr.substring(startPos, endPos + 1);
            }
        }
        return "";
    }
}
