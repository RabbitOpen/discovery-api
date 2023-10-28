package rabbit.discovery.api.rest;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.*;
import rabbit.discovery.api.rest.anno.Group;
import rabbit.discovery.api.rest.exception.InvalidRequestException;
import rabbit.discovery.api.rest.exception.NoRequestFoundException;
import rabbit.discovery.api.rest.http.HttpRequest;
import rabbit.discovery.api.rest.reader.*;
import rabbit.flt.common.utils.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rest/open client 工程类
 */
public abstract class ClientFactory implements InvocationHandler {

    /**
     * 请求缓存
     */
    protected Map<Method, HttpRequest> requestCache = new ConcurrentHashMap<>();

    /**
     * 创建请求
     * @return
     */
    protected abstract HttpRequest createHttpRequest();

    /**
     * 复制一个请求
     * @param request
     * @return
     */
    protected abstract HttpRequest cloneRequest(HttpRequest request);

    /**
     * 获取请求执行对象
     * @return
     */
    protected abstract HttpRequestExecutor getRequestExecutor();

    /**
     * 从方法上读取请求信息
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
        } else {
            throw new InvalidRequestException(method);
        }
        return null;
    }

    protected void cacheHttpRequests(Class<?> clz) {
        for (Method method : clz.getDeclaredMethods()) {
            HttpRequest httpRequest = createHttpRequest(method);
            afterRequestCreated(httpRequest);
            requestCache.put(method, httpRequest);
        }
    }

    /**
     * createHttpRequest(method)  后置事件
     * @param request
     */
    protected void afterRequestCreated(HttpRequest request) {
        // do nothing;
    }

    /**
     * 读取请求分组信息
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
}
