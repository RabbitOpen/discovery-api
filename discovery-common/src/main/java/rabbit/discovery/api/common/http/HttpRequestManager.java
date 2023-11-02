package rabbit.discovery.api.common.http;

import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.utils.JsonUtils;
import rabbit.flt.common.utils.ResourceUtils;
import rabbit.flt.common.utils.StringUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import static rabbit.discovery.api.common.enums.HttpMethod.GET;

public class HttpRequestManager {

    private static final HttpRequestManager requestManager = new HttpRequestManager();

    private HttpRequestManager() {}

    /**
     * 发起http请求
     *
     * @param request
     * @param resultType
     * @param <T>
     * @return
     */
    public static <T> T doRequest(Request request, Type resultType) {
        try {
            HttpURLConnection connection = requestManager.getHttpConnection(request);
            int responseCode = connection.getResponseCode();
            String charset = getResponseCharset(connection);
            if (200 != responseCode) {
                StringBuilder sb = requestManager.readContent(connection.getErrorStream(), charset);
                connection.disconnect();
                throw new DiscoveryException(sb.toString());
            }
            StringBuilder sb = requestManager.readContent(connection.getInputStream(), charset);
            connection.disconnect();
            if (String.class == resultType) {
                return (T) sb.toString();
            }
            return JsonUtils.readValue(sb.toString(), resultType);
        } catch (IOException e) {
            String message = "request[".concat(request.getRequestUrl()).concat("] error:").concat(e.getMessage());
            throw new DiscoveryException(message, e);
        }
    }

    private static String getResponseCharset(HttpURLConnection connection) {
        String contentType = connection.getHeaderField("Content-Type");
        if (!StringUtils.isEmpty(contentType) && contentType.toLowerCase().contains("charset")) {
            return contentType.split(";")[1].split("=")[1].trim();
        }
        return null;
    }

    private StringBuilder readContent(InputStream stream, String charSet) throws IOException {
        InputStreamReader in;
        if (StringUtils.isEmpty(charSet)) {
            in = new InputStreamReader(stream);
        } else {
            in = new InputStreamReader(stream, charSet);
        }
        BufferedReader reader = new BufferedReader(in);
        try {
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (null == line) {
                    break;
                }
                sb.append(line);
            }
            return sb;
        } finally {
            ResourceUtils.close(reader);
        }
    }

    private HttpURLConnection getHttpConnection(Request request) throws IOException {
        URL url = new URL(request.getRequestUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(request.getMethod().name());
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setConnectTimeout(5 * 1000);
        connection.setReadTimeout(10 * 1000);
        request.getHeaders().forEach(connection::setRequestProperty);
        connection.connect();
        if (null != request.getBody() && GET != request.getMethod()) {
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(JsonUtils.writeObject(request.getBody()));
            writer.flush();
        }
        return connection;
    }
}
