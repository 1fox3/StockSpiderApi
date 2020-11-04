package com.fox.spider.stock.util;

import java.io.UnsupportedEncodingException;

/**
 * 字符串编码格式转换
 *
 * @author lusongsong
 * @date 2020/11/4 15:52
 */
public class CharsetUtil {

    /**
     * GBK转UTF-8
     *
     * @param gbkStr
     * @return
     */
    public static String convertGBKToUtf8(String gbkStr) {
        try {
            int n = gbkStr.length();
            byte[] utfBytes = new byte[3 * n];
            int k = 0;
            for (int i = 0; i < n; i++) {
                int m = gbkStr.charAt(i);
                if (m < 128 && m >= 0) {
                    utfBytes[k++] = (byte) m;
                    continue;
                }
                utfBytes[k++] = (byte) (0xe0 | (m >> 12));
                utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
                utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
            }
            if (k < utfBytes.length) {
                byte[] tmp = new byte[k];
                System.arraycopy(utfBytes, 0, tmp, 0, k);
                return new String(tmp, "UTF-8");
            } else {
                return new String(utfBytes, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            throw new InternalError();
        }
    }
}
