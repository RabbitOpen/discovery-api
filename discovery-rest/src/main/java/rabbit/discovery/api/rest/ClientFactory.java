package rabbit.discovery.api.rest;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.*;
import rabbit.discovery.api.common.http.anno.Header;
import rabbit.discovery.api.common.http.anno.HeaderMap;
import rabbit.discovery.api.common.http.anno.Headers;
import rabbit.discovery.api.rest.anno.AcceptGZipEncoding;
import rabbit.discovery.api.rest.anno.Group;
import rabbit.discovery.api.rest.exception.InvalidRequestException;
import rabbit.discovery.api.rest.exception.NoRequestFoundException;
import rabbit.discovery.api.rest.http.HttpRequest;
import rabbit.discovery.api.rest.reader.*;
import rabbit.flt.common.utils.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * rest/open client 工程类
 */
public abstract class ClientFactory implements InvocationHandler, FactoryBean {

    /**
     * 被代理的接口class
     */
    protected Class<?> type;

    /**
     * 属性阅读器
     */
    protected Function<String, String> propertyReader;

    /**
     * 请求缓存
     */
    protected Map<Method, HttpRequest> requestCache = new ConcurrentHashMap<>();

    public ClientFactory(Class<?> type, Function<String, String> propertyReader) {
        this.type = type;
        this.propertyReader = propertyReader;
    }

    /**
     * 创建请求
     *
     * @return
     */
    protected abstract HttpRequest createHttpRequest();

    /**
     * 复制一个请求
     *
     * @param request
     * @return
     */
    protected abstract HttpRequest cloneRequest(HttpRequest request);

    /**
     * 获取请求执行对象
     *
     * @return
     */
    protected abstract HttpRequestExecutor getRequestExecutor();

    @Override
    public final Class<?> getObjectType() {
        return type;
    }

    @Override
    public final boolean isSingleton() {
        return true;
    }

    /**
     * 从方法上读取请求信息
     *
     * @param method
     * @return
     */
    protected final HttpRequest createHttpRequest(Method method) {
        MappingReader reader = new GetMappingReader(AnnotationUtils.findAnnotation(method, GetMapping.class));
        if (reader.isValidRequest()) {
            return reader.getRequest(createHttpRequest());
        }
        reader = new PostMappingReader(AnnotationUtils.findAnnotation(method, PostMapping.class));
        if (reader.isValidRequest()) {
            return reader.getRequest(createHttpRequest());
        }
        reader = new DeleteMappingReader(AnnotationUtils.findAnnotation(method, DeleteMapping.class));
        if (reader.isValidRequest()) {
            return reader.getRequest(createHttpRequest());
        }
        reader = new PatchMappingReader(AnnotationUtils.findAnnotation(method, PatchMapping.class));
        if (reader.isValidRequest()) {
            return reader.getRequest(createHttpRequest());
        }
        reader = new PutMappingReader(AnnotationUtils.findAnnotation(method, PutMapping.class));
        if (reader.isValidRequest()) {
            return reader.getRequest(createHttpRequest());
        }
        reader = new RequestMappingReader(AnnotationUtils.findAnnotation(method, RequestMapping.class));
        if (reader.isValidRequest()) {
            return reader.getRequest(createHttpRequest());
        }
        throw new NoRequestFoundException(method);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("toString".equals(method.getName())) {
            return toString();
        }
        if (requestCache.containsKey(method)) {
            HttpRequest httpRequest = cloneRequest(requestCache.get(method));
            httpRequest.setMethod(method);
            Parameter[] parameters = method.getParameters();
            httpRequest.setPathVariables(readPathVariables(args, parameters));
            httpRequest.setBody(readRequestBody(args, parameters));
            httpRequest.setQueryParameters(readParameters(args, parameters));
            httpRequest.setHeaders(readHttpHeaders(method, args, parameters));
            httpRequest.setApplicationGroup(readGroup(args, parameters));
            return getRequestExecutor().execute(httpRequest);
        } else {
            throw new InvalidRequestException(method);
        }
    }

    /**
     * 缓存接口下方法对应的请求对象
     */
    protected void cacheHttpRequests() {
        for (Method method : type.getDeclaredMethods()) {
            HttpRequest httpRequest = createHttpRequest(method);
            afterRequestCreated(httpRequest);
            requestCache.put(method, httpRequest);
        }
    }

    /**
     * 读取el表达式属性配置
     * @param property
     * @return
     */
    protected String readConfigProperty(String property) {
        if (property.startsWith("${") && property.endsWith("}")) {
            return propertyReader.apply(property.substring(2, property.length() - 1));
        } else {
            return property;
        }
    }

    /**
     * createHttpRequest(method)  后置事件
     *
     * @param request
     */
    protected void afterRequestCreated(HttpRequest request) {
        // do nothing;
    }

    /**
     * 读取请求分组信息
     *
     * @param args
     * @param parameters
     * @return
     */
    protected String readGroup(Object[] args, Parameter[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            Parameter para = parameters[i];
            if (null != para.getAnnotation(Group.class)) {
                return StringUtils.toString(args[i]);
            }
        }
        return null;
    }

    /**
     * 读取request body
     *
     * @param args
     * @param parameters
     * @return
     */
    protected Object readRequestBody(Object[] args, Parameter[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            Parameter para = parameters[i];
            if (null != para.getAnnotation(RequestBody.class)) {
                return args[i];
            }
        }
        return null;
    }

    /**
     * 读取http 头
     *
     * @param method
     * @param args
     * @param parameters
     * @return
     */
    protected Map<String, String> readHttpHeaders(Method method, Object[] args, Parameter[] parameters) {
        Map<String, String> headerMap = new HashMap<>();
        if (null != method.getAnnotation(AcceptGZipEncoding.class) ||
                null != method.getDeclaringClass().getAnnotation(AcceptGZipEncoding.class)) {
            headerMap.put("Accept-Encoding", "gzip");
        }
        Header header = method.getAnnotation(Header.class);
        if (null != header) {
            headerMap.put(header.name().trim(), header.value().trim());
        }
        Headers headers = method.getAnnotation(Headers.class);
        if (null != headers) {
            for (Header h : headers.value()) {
                headerMap.put(h.name().trim(), h.value().trim());
            }
        }
        for (int i = 0; i < parameters.length; i++) {
            Parameter para = parameters[i];
            RequestHeader rh = AnnotationUtils.findAnnotation(para, RequestHeader.class);
            if (null != rh && null != args[i]) {
                headerMap.put(rh.name().trim(), args[i].toString().trim());
            }
            Header h = AnnotationUtils.findAnnotation(para, Header.class);
            if (null != h && null != args[i]) {
                headerMap.put(h.name().trim(), args[i].toString().trim());
            }
            HeaderMap hm = AnnotationUtils.findAnnotation(para, HeaderMap.class);
            if (null != hm && null != args[i] && args[i] instanceof Map) {
                ((Map) args[i]).forEach((k, v) -> headerMap.put(k.toString().trim(), v.toString().trim()));
            }
        }
        return headerMap;
    }

    /**
     * 读取请求参数
     *
     * @param args
     * @param parameters
     * @return
     */
    protected Map<String, String> readParameters(Object[] args, Parameter[] parameters) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter para = parameters[i];
            RequestParam pv = para.getAnnotation(RequestParam.class);
            if (null != pv) {
                map.put(pv.value().trim(), args[i].toString());
            }
        }
        return map;
    }

    /**
     * 读取path变量
     *
     * @param args
     * @param parameters
     * @return
     */
    protected Map<String, String> readPathVariables(Object[] args, Parameter[] parameters) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter para = parameters[i];
            PathVariable pv = para.getAnnotation(PathVariable.class);
            if (null != pv) {
                map.put(pv.value().trim(), args[i].toString());
            }
        }
        return map;
    }

    public void setPropertyReader(Function<String, String> propertyReader) {
        this.propertyReader = propertyReader;
    }

    @Override
    public String toString() {
        return type.getSimpleName();
    }

}
