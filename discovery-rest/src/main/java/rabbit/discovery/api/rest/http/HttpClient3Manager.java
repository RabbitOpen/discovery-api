package rabbit.discovery.api.rest.http;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import rabbit.discovery.api.common.exception.RestApiException;
import rabbit.discovery.api.rest.HttpClientManager;
import rabbit.discovery.api.rest.anno.ReadTimeout;
import rabbit.flt.common.utils.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static rabbit.discovery.api.common.enums.HttpMethod.*;

/**
 * http client 3 连接管理对象
 */
public class HttpClient3Manager extends HttpClientManager<HttpMethodBase> {

    protected HttpConnectionManager connectionManager;

    private HttpClient httpClient;

    /**
     * 初始化
     */
    @Override
    protected void initConnectionManager() {
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.setParams(getConnectionManagerParams());
        httpClient = new HttpClient(connectionManager);
    }

    /**
     * 构建连接池参数
     *
     * @return
     */
    protected HttpConnectionManagerParams getConnectionManagerParams() {
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        // 最大总连接
        params.setMaxTotalConnections(getConfiguration().getMaxConnection());
        // 每个host最大连接
        params.setDefaultMaxConnectionsPerHost(getConfiguration().getMaxConnectionPerHost());
        // 建立连接超时
        params.setConnectionTimeout(getConfiguration().getConnectionTimeout());
        // 读超时
        params.setSoTimeout(getConfiguration().getReadTimeout());
        return params;
    }

    @Override
    protected void doShutdown() {
        ((MultiThreadedHttpConnectionManager) connectionManager).shutdown();
        logger.info("http client manager is closed!");
    }

    @Override
    protected HttpResponse doRequest(HttpRequest requestObj) {
        HttpMethodBase request = getRequestObject(requestObj);
        InputStream stream = null;
        try {
            ReadTimeout readTimeout = requestObj.getMethod().getAnnotation(ReadTimeout.class);
            if (null != readTimeout) {
                HttpMethodParams params = request.getParams();
                params.setSoTimeout(readTimeout.value());
            }
            int statusCode = httpClient.executeMethod(request);
            Map<String, String> headerMap = getResponseHeaderMap(request);
            stream = request.getResponseBodyAsStream();
            byte[] bytes = readContent(stream, getResponseContentLength(request));
            return new HttpResponse(new String(unzipIfZipped(headerMap, bytes)), headerMap, statusCode);
        } catch (Exception e) {
            throw new RestApiException(e);
        } finally {
            ResourceUtils.close(stream);
        }
    }

    /**
     * 获取响应体长度
     * @param request
     * @return
     */
    private int getResponseContentLength(HttpMethodBase request) {
        for (Header responseHeader : request.getResponseHeaders()) {
            if ("content-length".equals(responseHeader.getName())) {
                return Integer.parseInt(responseHeader.getValue());
            }
        }
        return 1024;
    }

    /**
     * 获取response header
     * @param request
     * @return
     */
    private Map<String, String> getResponseHeaderMap(HttpMethodBase request) {
        Map<String, String> headerMap = new HashMap<>();
        for (Header responseHeader : request.getResponseHeaders()) {
            headerMap.put(responseHeader.getName(), responseHeader.getValue());
        }
        return headerMap;
    }

    /**
     * 读取response body
     *
     * @param stream
     * @param contentLength
     * @return
     * @throws IOException
     */
    private byte[] readContent(InputStream stream, int contentLength) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            byte[] bytes = new byte[contentLength];
            while (true) {
                int read = stream.read(bytes, 0, bytes.length);
                if (read > 0) {
                    os.write(bytes, 0, read);
                } else {
                    break;
                }
            }
            return os.toByteArray();
        } finally {
            ResourceUtils.close(os);
        }
    }

    @Override
    protected void setRequestBody(HttpMethodBase request, String body, String contentType) {
        if (request instanceof EntityEnclosingMethod) {
            try {
                String ct = "application/json";
                String charset = "UTF-8";
                if (2 == contentType.split(";").length && 2 == contentType.split(";")[1].split("=").length) {
                    ct = contentType.split(";")[0].trim();
                    charset = contentType.split(";")[1].split("=")[1].trim();
                }
                StringRequestEntity requestEntity = new StringRequestEntity(body, ct, charset);
                ((EntityEnclosingMethod) request).setRequestEntity(requestEntity);
            } catch (UnsupportedEncodingException e) {
                throw new RestApiException(e);
            }
        }
    }

    /**
     * 获取请求对象
     * @param request
     * @return
     */
    protected HttpMethodBase getRequestObject(HttpRequest request) {
        HttpMethodBase requestMethod = getHttpMethodBase(request);
        addRequestBody(request, requestMethod);
        request.getHeaders().forEach(requestMethod::addRequestHeader);
        return requestMethod;
    }

    private HttpMethodBase getHttpMethodBase(HttpRequest request) {
        if (POST == request.getHttpMethod()) {
            return new PostMethod(request.getUri());
        }
        if (GET == request.getHttpMethod()) {
            return new GetMethod(request.getUri());
        }
        if (PUT == request.getHttpMethod()) {
            return new PutMethod(request.getUri());
        }
        if (DELETE == request.getHttpMethod()) {
            return new DeleteMethod(request.getUri());
        }
        throw new RestApiException("unsupported method type: ".concat(request.getHttpMethod().name()));
    }
}
