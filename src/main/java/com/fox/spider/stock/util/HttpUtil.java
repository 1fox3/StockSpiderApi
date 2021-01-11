package com.fox.spider.stock.util;

import com.fox.spider.stock.entity.dto.http.HttpResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * @author lusongsong
 * @date 2020/11/4 15:52
 */
public class HttpUtil {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 正常响应错误码
     */
    public static final Integer CODE_SUCCESS = 200;
    /**
     * GET请求方式
     */
    public static final String METHOD_GET = "GET";
    /**
     * POST请求方式
     */
    public static final String METHOD_POST = "POST";
    /**
     * UTF8字符编码
     */
    public static final String CHARSET_UTF8 = "UTF-8";
    /**
     * GBK字符编码
     */
    public static final String CHARSET_GBK = "GBK";
    /**
     * 请求头参数方式KEY
     */
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * 请求头参数方式JSON
     */
    public static final String CONTENT_TYPE_JSON = "application/json";
    /**
     * 请求头参数方式参数
     */
    public static final String CONTENT_TYPE_PARAM = "application/x-www-form-urlencoded";
    /**
     * 请求头参数方式表单
     */
    public static final String CONTENT_TYPE_FORM = "multipart/form-data";
    /**
     * 请求参数方式JSON
     */
    public static final String PARAM_TYPE_JSON = "JSON";
    /**
     * 请求参数方式参数
     */
    public static final String PARAM_TYPE_PARAM = "PARAM";
    /**
     * 请求参数方式表单
     */
    public static final String PARAM_TYPE_FORM = "FORM";
    /**
     * 支持的请求方式
     */
    private static final ArrayList<String> methodScope = new ArrayList<>(
            Arrays.asList(METHOD_GET, METHOD_POST)
    );
    /**
     * 数据类型
     */
    private static final Map<String, String> CONTENT_TYPE_MAP = new HashMap<String, String>() {{
        put(PARAM_TYPE_JSON, CONTENT_TYPE_JSON);
        put(PARAM_TYPE_PARAM, CONTENT_TYPE_PARAM);
        put(PARAM_TYPE_FORM, CONTENT_TYPE_FORM);
    }};
    /**
     * 请求链接
     */
    private String url;
    /**
     * 请求方式
     */
    private String method = METHOD_GET;
    /**
     * 请求参数类型
     */
    private String paramType = PARAM_TYPE_PARAM;
    /**
     * 请求参数
     */
    private Map<String, Object> params = new HashMap<>();
    /**
     * 请求数据
     */
    private String body = "";
    /**
     * 请求头
     */
    private Map<String, String> headers = new HashMap<>();
    /**
     * 请求返回数据的目标字符集
     */
    private String desCharset = CHARSET_UTF8;
    /**
     * 请求返回数据的初始字符集
     */
    private String oriCharset = CHARSET_UTF8;
    /**
     * 请求出现非200请求时，错误返回数据的原始字符集
     */
    private String errorOriCharset = CHARSET_UTF8;
    /**
     * 连接超时时间
     */
    private int connectTimeout = 60000;
    /**
     * 数据传输超时时间
     */
    private int readTimeout = 60000;
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 结束时间
     */
    private long endTime;

    /**
     * 设置请求链接
     *
     * @param url
     * @return
     */
    public HttpUtil setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * 获取请求链接
     *
     * @return
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * 获取带参数的请求链接
     *
     * @return
     */
    public String getRequestUrl(String requestBody) {
        if (!METHOD_GET.equals(this.getMethod())) {
            return this.getUrl();
        }
        if (null != requestBody && !requestBody.equals("")) {
            return this.getUrl() + (this.getUrl().contains("?") ? "" : "?") + requestBody;
        }
        return this.getUrl();
    }

    /**
     * 设置请求方式
     *
     * @param method
     * @return
     */
    public HttpUtil setMethod(String method) {
        String upMethod = method.toUpperCase();
        if (HttpUtil.methodScope.contains(upMethod)) {
            this.method = upMethod;
        }
        return this;
    }

    /**
     * 获取请求方式
     *
     * @return
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * 设置请求参数方式
     *
     * @param paramType
     * @return
     */
    public HttpUtil setParamType(String paramType) {
        String upParamType = paramType.toUpperCase();
        if (CONTENT_TYPE_MAP.containsKey(upParamType)) {
            this.paramType = paramType;
        }
        return this;
    }

    /**
     * 获取请求参数方式
     *
     * @return
     */
    public String getParamType() {
        return this.paramType.toUpperCase();
    }

    /**
     * 获取请求头中需要的参数方式
     *
     * @return
     */
    public String getContentType() {
        if (CONTENT_TYPE_MAP.containsKey(this.getParamType())) {
            return CONTENT_TYPE_MAP.get(this.getParamType());
        }
        return CONTENT_TYPE_PARAM;
    }

    /**
     * 设置请求数据
     *
     * @param body
     * @return
     */
    public HttpUtil setBody(String body) {
        this.body = body;
        return this;
    }

    /**
     * 获取请求数据
     *
     * @return
     */
    public String getBody() {
        return this.body;
    }

    /**
     * 获取请求体字符串
     *
     * @return
     */
    public String bodyToStr() {
        return this.body.toString();
    }

    /**
     * 设置请求参数
     *
     * @param params
     * @return
     */
    public HttpUtil setParams(Map<String, Object> params) {
        if (null == this.params || this.params.isEmpty()) {
            this.params = params;
        } else {
            this.params.putAll(params);
        }
        return this;
    }

    /**
     * 设置请求参数
     *
     * @param key
     * @param value
     * @return
     */
    public HttpUtil setParam(String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    /**
     * 移除某个请求参数
     *
     * @param key
     * @return
     */
    public HttpUtil removeParam(String key) {
        this.params.remove(key);
        return this;
    }

    /**
     * 清除请求参数
     *
     * @return
     */
    public HttpUtil clearParam() {
        this.params.clear();
        return this;
    }

    /**
     * 获取消耗的时间
     *
     * @return
     */
    public long getRequestTime() {
        return this.endTime - this.startTime;
    }

    /**
     * 获取请求参数
     *
     * @return
     */
    public Map<String, Object> getParams() {
        return this.params;
    }

    /**
     * 请求参数拼接
     *
     * @return
     */
    public String paramsToUrlParams() {
        if (!this.params.isEmpty()) {
            LinkedList<String> paramsList = new LinkedList<>();
            for (String key : this.params.keySet()) {
                paramsList.add(key + "=" + this.params.get(key));
            }
            return StringUtils.join(paramsList.toArray(), "&");
        }
        return "";
    }

    /**
     * 设置请求头
     *
     * @param headers
     * @return
     */
    public HttpUtil setHeader(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * 设置请求头
     *
     * @param key
     * @param value
     * @return
     */
    public HttpUtil setHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    /**
     * 移除某个请求头
     *
     * @param key
     * @return
     */
    public HttpUtil removeHeader(String key) {
        this.headers.remove(key);
        return this;
    }

    /**
     * 清除请求头
     *
     * @return
     */
    public HttpUtil clearHeader() {
        this.headers.clear();
        return this;
    }

    /**
     * 获取请求头
     *
     * @return
     */
    public Map<String, String> getHeader() {
        return this.headers;
    }

    /**
     * 设置请求返回初始字符集
     *
     * @param charset
     * @return
     */
    public HttpUtil setOriCharset(String charset) {
        this.oriCharset = charset;
        return this;
    }

    /**
     * 请求出现非200请求时，错误返回数据的原始字符集
     *
     * @param charset
     * @return
     */
    public HttpUtil setErrorOriCharset(String charset) {
        this.errorOriCharset = charset;
        return this;
    }

    /**
     * 获取请求返回初始字符集
     *
     * @return
     */
    public String getOriCharset() {
        return this.oriCharset;
    }

    /**
     * 设置请求返回目标字符集
     *
     * @param charset
     * @return
     */
    public HttpUtil setDesCharset(String charset) {
        this.desCharset = charset;
        return this;
    }

    /**
     * 获取请求返回目标字符集
     *
     * @return
     */
    public String getDesCharset() {
        return this.desCharset;
    }

    /**
     * 处理HTTP请求
     *
     * @return
     */
    public HttpResponseDto request() throws IOException {
        //请求信息
        String requestBody = "";
        if (!this.getBody().equals("")) {
            requestBody = this.bodyToStr();
        }
        if (!this.getParams().isEmpty()) {
            requestBody = this.paramsToUrlParams();
        }
        this.startTime = System.currentTimeMillis();
        //初始化
        URL urlObj = new URL(this.getRequestUrl(requestBody));
        HttpURLConnection urlCon = (HttpURLConnection) urlObj.openConnection();
        //设置超时时间
        urlCon.setReadTimeout(this.readTimeout);
        urlCon.setConnectTimeout(this.connectTimeout);
        //设置请求方法
        urlCon.setRequestMethod(this.getMethod());
        //添加请求头
        if (!this.headers.isEmpty()) {
            for (String key : this.headers.keySet()) {
                urlCon.setRequestProperty(key, this.headers.get(key));
            }
        }
        if (!this.headers.containsKey(CONTENT_TYPE)) {
            urlCon.setRequestProperty(CONTENT_TYPE, this.getContentType());
        }
        //GET请求无需发送数据
        if (METHOD_GET.equals(this.getMethod())) {
            return readResponse(urlCon);
        }
        //允许输入输出
        urlCon.setDoOutput(true);
        urlCon.setDoInput(true);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlCon.getOutputStream(), CHARSET_UTF8));
        if (requestBody != null) {
            //写入请求内容
            bw.write(requestBody);
        }
        bw.close();
        return readResponse(urlCon);
    }

    /**
     * 读取接口返回数据
     *
     * @param urlCon
     * @return
     * @throws IOException
     */
    private HttpResponseDto readResponse(HttpURLConnection urlCon) throws IOException {
        try {
            Map<String, List<String>> headerMap = urlCon.getHeaderFields();
            String encodingTypeHeader = "Content-Encoding";
            boolean isGZip = false;
            if (null != headerMap && headerMap.containsKey(encodingTypeHeader)) {
                List<String> contentEncodingList = headerMap.get(encodingTypeHeader);
                if (contentEncodingList.get(0).equals("gzip")) {
                    isGZip = true;
                }
            }
            BufferedReader br;
            if (200 == urlCon.getResponseCode()) {
                if (isGZip) {
                    GZIPInputStream gzin = new GZIPInputStream(urlCon.getInputStream());
                    br = new BufferedReader(new InputStreamReader(gzin, "GB2312"));
                } else {
                    br = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), this.oriCharset));
                }
            } else {
                br = new BufferedReader(new InputStreamReader(urlCon.getErrorStream(), this.errorOriCharset));
            }

            StringBuilder sb = new StringBuilder();
            String read = null;
            while ((read = br.readLine()) != null) {
                sb.append(read);
                sb.append("\n");
            }
            br.close();
            String response = sb.toString();
            if (!this.oriCharset.equals(this.desCharset)) {
                if (this.oriCharset.equals(CHARSET_GBK) && this.desCharset.equals(CHARSET_UTF8)) {
                    response = CharsetUtil.convertGBKToUtf8(response);
                }
            }
            HttpResponseDto httpResponseDto = new HttpResponseDto(urlCon.getResponseCode(), urlCon.getResponseMessage(),
                    urlCon.getHeaderFields(), urlCon.getURL().toString(), response);
            if (!CODE_SUCCESS.equals(httpResponseDto.getCode())) {
                logger.error(httpResponseDto.toString());
            }
            return httpResponseDto;
        } catch (Exception e) {
            return new HttpResponseDto(urlCon.getResponseCode(), urlCon.getResponseMessage(),
                    urlCon.getHeaderFields(), urlCon.getURL().toString(), "");
        } finally {
            urlCon.disconnect();
            this.endTime = System.currentTimeMillis();
        }
    }
}



