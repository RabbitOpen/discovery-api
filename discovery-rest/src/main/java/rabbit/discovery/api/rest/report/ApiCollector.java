package rabbit.discovery.api.rest.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.*;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.TraceConfiguration;
import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.common.rpc.ApiDescription;
import rabbit.discovery.api.common.rpc.ApiReportService;
import rabbit.discovery.api.common.utils.PathParser;
import rabbit.discovery.api.rest.anno.Declaration;
import rabbit.discovery.api.rest.reader.*;

import java.lang.reflect.Method;
import java.util.*;

import static rabbit.discovery.api.common.enums.HttpMethod.*;
import static rabbit.discovery.api.rest.Policy.INCLUDE;

/**
 * 接口采集服务
 */
public class ApiCollector implements BeanPostProcessor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Configuration configuration;

    @Autowired
    private TraceConfiguration traceConfiguration;

    private Set<Class<?>> beanClzSet = new HashSet<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        List<ApiDescription> apiList = new ArrayList<>();
        apiList.addAll(getApis(bean));
        doReport(bean.getClass().getName(), apiList);
        return bean;
    }

    /**
     * 获取bean上定义的接口
     * @param bean
     * @return
     */
    private List<ApiDescription> getApis(Object bean) {
        List<ApiDescription> apiList = new ArrayList<>();
        Class<?> beanClz = bean.getClass();
        Declaration declaration = beanClz.getAnnotation(Declaration.class);
        if (beanClzSet.contains(beanClz) || null == declaration) {
            return apiList;
        }
        beanClzSet.add(beanClz);
        getAllMethods(beanClz).forEach(method -> {
            if (!shouldReport(declaration, method)) {
                return;
            }
            readApiFromMethod(beanClz, method).forEach(api -> {
                api.setPath(PathParser.removeRepeatedSeparator(api.getPath()));
                apiList.add(api);
            });
        });
        return apiList;
    }

    /**
     * 判断是否需要上报
     * @param declaration
     * @param method
     * @return
     */
    private boolean shouldReport(Declaration declaration, Method method) {
        List<String> methods = Arrays.asList(declaration.methods());
        if (INCLUDE == declaration.policy()) {
            return methods.contains(method.getName());
        } else {
            return !methods.contains(method.getName());
        }
    }

    /**
     * 获取类上所有方法
     * @param beanClz
     * @return
     */
    private List<Method> getAllMethods(Class<?> beanClz) {
        List<Method> methods = new ArrayList<>();
        Class<?> clz = beanClz;
        while (true) {
            methods.addAll(Arrays.asList(clz.getDeclaredMethods()));
            if (clz.getSuperclass() == Object.class) {
                break;
            }
            clz = clz.getSuperclass();
        }
        return methods;
    }

    /**
     * 上报
     * @param clzName
     * @param apiList
     */
    private void doReport(String clzName, List<ApiDescription> apiList) {
        if (apiList.isEmpty()) {
            return;
        }
        try {
            ApiReportService reportService = getApiReportService();
            reportService.setAgentServer(traceConfiguration.getServers());
            reportService.setSecurityKey(traceConfiguration.getSecurityKey());
            reportService.doReport(configuration.getApplicationCode(), clzName, apiList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private ApiReportService getApiReportService() {
        Iterator<ApiReportService> iterator = ServiceLoader.load(ApiReportService.class).iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return new ReportServiceProxy();
    }

    /**
     * 读取method上的接口
     * @param beanClz
     * @param method
     * @return
     */
    private List<ApiDescription> readApiFromMethod(Class<?> beanClz, Method method) {
        String basePath = getBasePath(beanClz);
        List<ApiDescription> apiList = new ArrayList<>();
        GetMapping getMapping = AnnotationUtils.findAnnotation(method, GetMapping.class);
        if (null != getMapping) {
            List<String> paths = new GetMappingReader(getMapping).getDeclaredPaths();
            paths.forEach(p -> apiList.add(createApi(beanClz, method, basePath.concat(p), GET)));
            return apiList;
        }
        PostMapping postMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
        if (null != postMapping) {
            List<String> paths = new PostMappingReader(postMapping).getDeclaredPaths();
            paths.forEach(p -> apiList.add(createApi(beanClz, method, basePath.concat(p), POST)));
            return apiList;
        }
        PutMapping putMapping = AnnotationUtils.findAnnotation(method, PutMapping.class);
        if (null != putMapping) {
            List<String> paths = new PutMappingReader(putMapping).getDeclaredPaths();
            paths.forEach(p -> apiList.add(createApi(beanClz, method, basePath.concat(p), PUT)));
            return apiList;
        }
        PatchMapping patchMapping = AnnotationUtils.findAnnotation(method, PatchMapping.class);
        if (null != patchMapping) {
            List<String> paths = new PatchMappingReader(patchMapping).getDeclaredPaths();
            paths.forEach(p -> apiList.add(createApi(beanClz, method, basePath.concat(p), PATCH)));
            return apiList;
        }
        DeleteMapping deleteMapping = AnnotationUtils.findAnnotation(method, DeleteMapping.class);
        if (null != deleteMapping) {
            List<String> paths = new DeleteMappingReader(deleteMapping).getDeclaredPaths();
            paths.forEach(p -> apiList.add(createApi(beanClz, method, basePath.concat(p), DELETE)));
            return apiList;
        }
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        if (null != requestMapping) {
            List<String> paths = new RequestMappingReader(requestMapping).getDeclaredPaths();
            paths.forEach(p -> apiList.add(createApi(beanClz, method, basePath.concat(p), GET)));
            return apiList;
        }
        return apiList;
    }

    private ApiDescription createApi(Class<?> beanClz, Method method, String path, HttpMethod httpMethod) {
        ApiDescription desc = new ApiDescription();
        desc.setName(beanClz.getName() + "." + method.getName());
        desc.setMethod(httpMethod);
        desc.setPath(path);
        return desc;
    }

    /**
     * 获取基础路径
     * @param beanClz
     * @return
     */
    private String getBasePath(Class<?> beanClz) {
        RequestMapping mapping = AnnotationUtils.findAnnotation(beanClz, RequestMapping.class);
        if (null != mapping) {
            String path = new RequestMappingReader(mapping).getDeclaredPaths().get(0);
            return configuration.getContextPath().concat(path);
        }
        return configuration.getContextPath();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
