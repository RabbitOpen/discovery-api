package rabbit.discovery.api.rest;


import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.common.utils.PathParser;
import rabbit.discovery.api.rest.http.HttpRequest;
import rabbit.flt.common.utils.CollectionUtils;
import rabbit.flt.common.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public abstract class MappingReader<T extends Annotation> {

    protected T mapping;

    public MappingReader(T mapping) {
        this.mapping = mapping;
    }

    /**
     * 获取声明的请求路径
     *
     * @return
     */
    public List<String> getDeclaredPaths() {
        List<String[]> groups = getDeclaredPathGroups();
        for (String[] paths : groups) {
            if (hasValidPath(paths)) {
                return Arrays.asList(paths);
            }
        }
        return Arrays.asList("");
    }

    private boolean hasValidPath(String[] paths) {
        if (CollectionUtils.isEmpty(paths)) {
            return false;
        }
        for (String path : paths) {
            if (!StringUtils.isEmpty(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取声明的请求路径组
     *
     * @return
     */
    protected abstract List<String[]> getDeclaredPathGroups();

    /**
     * 获取方法类型
     *
     * @return
     */
    protected abstract HttpMethod getHttpMethod();

    /**
     * 包装request
     *
     * @param request
     * @return
     */
    public final HttpRequest getRequest(HttpRequest request) {
        String path = getDeclaredPaths().get(0);
        if (!path.startsWith("/")) {
            path = "/".concat(path);
        }
        request.setUri(PathParser.removeRepeatedSeparator(path));
        request.setHttpMethod(getHttpMethod());
        return request;
    }

    public boolean isValidRequest() {
        return null != mapping;
    }
}
