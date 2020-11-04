package com.fox.spider.stock.entity.dto.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 接口响应返回类
 *
 * @author lusongsong
 * @date 2020/11/4 16:28
 */
@Data
public class HttpResponseDto {
    /**
     * 错误码
     */
    Integer code;
    /**
     * 错误信息
     */
    String msg;
    /**
     * 请求响应头
     */
    Map<String, List<String>> headers;
    /**
     * 请求地址
     */
    String requestUrl;
    /**
     * 接口返回
     */
    String content;

    /**
     * 构造函数
     *
     * @param code
     * @param msg
     * @param headers
     * @param requestUrl
     * @param content
     */
    public HttpResponseDto(int code, String msg, Map<String, List<String>> headers, String requestUrl, String content) {
        this.code = code;
        this.msg = msg;
        this.headers = headers;
        this.requestUrl = requestUrl;
        this.content = content;
    }

    /**
     * 接口返回转对象
     *
     * @param clz
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T getContent(Class<T> clz) throws IOException {
        if (StringUtils.isNotBlank(content)) {
            return new ObjectMapper().readValue(content, clz);
        }
        return null;
    }
}
