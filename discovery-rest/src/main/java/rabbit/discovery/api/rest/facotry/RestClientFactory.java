package rabbit.discovery.api.rest.facotry;

import org.springframework.beans.factory.annotation.Autowired;
import rabbit.discovery.api.common.ApiProtocolHelper;
import rabbit.discovery.api.common.utils.PathParser;
import rabbit.discovery.api.rest.ClientFactory;
import rabbit.discovery.api.rest.HttpRequestExecutor;
import rabbit.discovery.api.rest.anno.RestClient;
import rabbit.discovery.api.rest.http.HttpRequest;
import rabbit.discovery.api.rest.http.RestClientExecutor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Function;

import static rabbit.flt.common.utils.StringUtils.isEmpty;

public class RestClientFactory extends ClientFactory {

    // 服务方应用
    private String application;

    // rest接口的context path
    private String contextPath;

    @Autowired
    private RestClientExecutor restClientExecutor;

    public RestClientFactory(Class<?> type) {
        super(type, null);
    }

    public RestClientFactory(String application, Class<?> objectType, RestClientExecutor restClientExecutor,
                             Function<String, String> propertyReader) {
        this(objectType);
        setPropertyReader(propertyReader);
        this.restClientExecutor = restClientExecutor;
        setApplication(new String[]{application});
        this.contextPath = resolveContextPath(type.getAnnotation(RestClient.class));
        cacheHttpRequests();
    }

    /**
     * 解析context path，确保以"/"开头，不以"/"结尾
     *
     * @param client
     * @return
     */
    private String resolveContextPath(RestClient client) {
        String cp = client.contextPath();
        if (isEmpty(cp)) {
            return "";
        }
        cp = readConfigProperty(cp);
        if (!cp.startsWith("/")) {
            cp = "/".concat(cp);
        }
        cp = PathParser.removeRepeatedSeparator(cp);
        return cp.endsWith("/") ? cp.substring(0, cp.length() - 1) : cp;
    }

    @Override
    protected HttpRequest createHttpRequest() {
        return new HttpRequest(readConfigProperty(application), this);
    }

    /**
     * 拼接context path
     *
     * @param request
     * @param method
     */
    @Override
    protected void afterRequestCreated(HttpRequest request, Method method) {
        super.afterRequestCreated(request, method);
        request.setUri(this.contextPath.concat(request.getUri()));
    }

    /**
     * 克隆请求
     *
     * @param request
     * @return
     */
    @Override
    protected HttpRequest cloneRequest(HttpRequest request) {
        HttpRequest httpRequest = createHttpRequest();
        httpRequest.setMaxRetryTimes(request.getMaxRetryTimes());
        httpRequest.setMethod(request.getMethod());
        httpRequest.setHttpMethod(request.getHttpMethod());
        httpRequest.setUri(request.getUri());
        httpRequest.setResultType(request.getMethod().getGenericReturnType());
        ApiProtocolHelper.addProtocolHeader(httpRequest::setHeader, restClientExecutor.getConfiguration());
        return httpRequest;
    }

    @Override
    protected HttpRequestExecutor getRequestExecutor() {
        return restClientExecutor;
    }

    /**
     * 生成代理对象
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object getObject() {
        return Proxy.newProxyInstance(RestClientFactory.class.getClassLoader(), new Class[]{getObjectType()},
                new RestClientFactory(application, getObjectType(), restClientExecutor, propertyReader));
    }

    /**
     * spring 构建时会调用set方法
     *
     * @param application
     */
    public void setApplication(String[] application) {
        this.application = application[0];
    }
}
