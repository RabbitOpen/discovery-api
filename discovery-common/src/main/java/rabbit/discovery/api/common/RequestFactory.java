package rabbit.discovery.api.common;

import rabbit.discovery.api.common.http.GetRequest;
import rabbit.discovery.api.common.http.HttpRequestManager;
import rabbit.discovery.api.common.http.PostRequest;
import rabbit.discovery.api.common.http.Request;
import rabbit.discovery.api.common.http.anno.Headers;
import rabbit.discovery.api.common.http.anno.*;
import rabbit.discovery.api.common.utils.RsaUtils;
import rabbit.flt.common.utils.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

public class RequestFactory implements InvocationHandler {

    private Configuration configuration;

    private PrivateKey privateKey;

    private String name;

    private RequestFactory() {
    }

    /**
     * 代理请求
     *
     * @param clz
     * @param configuration
     * @param <T>
     * @return
     */
    public static <T> T proxy(Class<T> clz, Configuration configuration) {
        RequestFactory factory = new RequestFactory();
        factory.setConfiguration(configuration);
        factory.setName(clz.getName());
        return (T) Proxy.newProxyInstance(clz.getClassLoader(), new Class[]{clz}, factory);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if ("toString".equals(method.getName())) {
            return name;
        }
        Request request = createRequest(method);
        request.setPathVariables(getPathVariables(method, args));
        request.setBody(getRequestBody(method, args));
        request.setHeaders(getRequestHeaders(method, args));
        return HttpRequestManager.doRequest(request, method.getGenericReturnType());
    }

    /**
     * 获取请求body
     *
     * @param method
     * @param args
     * @return
     */
    private Object getRequestBody(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Body pv = parameter.getAnnotation(Body.class);
            if (null != pv) {
                return args[i];
            }
        }
        return null;
    }

    /**
     * 获取请求头
     *
     * @param method
     * @param args
     * @return
     */
    private Map<String, String> getRequestHeaders(Method method, Object[] args) {
        Map<String, String> headerMap = ApiProtocolHelper.getSignatureMap(configuration.getApplicationCode(), privateKey);
        Header header = method.getAnnotation(Header.class);
        if (null != header) {
            headerMap.put(header.name(), header.value());
        }
        Headers headers = method.getAnnotation(Headers.class);
        if (null != headers) {
            for (Header h : headers.value()) {
                headerMap.put(h.name(), h.value());
            }
        }
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            Header h = p.getAnnotation(Header.class);
            if (null != h && null != args[i]) {
                headerMap.put(h.name(), h.value());
                continue;
            }
            HeaderMap map = p.getAnnotation(HeaderMap.class);
            if (null != map && null != args[i]) {
                headerMap.putAll((Map<? extends String, ? extends String>) args[i]);
            }
        }
        return headerMap;
    }

    private Request createRequest(Method method) {
        String address = configuration.nextRegistryAddress();
        Post post = method.getAnnotation(Post.class);
        if (null != post) {
            return new PostRequest(address.concat(post.value()));
        } else {
            Get get = method.getAnnotation(Get.class);
            return new GetRequest(address.concat(get.value()));
        }
    }

    /**
     * 获取path变量值
     *
     * @param method
     * @param args
     * @return
     */
    private Map<String, String> getPathVariables(Method method, Object[] args) {
        Map<String, String> pathVariables = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            RequestPathVariable pv = parameter.getAnnotation(RequestPathVariable.class);
            if (null != pv) {
                pathVariables.put(pv.value(), StringUtils.toString(args[i]));
            }
        }
        return pathVariables;
    }

    private void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        this.privateKey = RsaUtils.loadPrivateKeyFromString(configuration.getPrivateKey());
    }

    private void setName(String name) {
        this.name = name;
    }
}
