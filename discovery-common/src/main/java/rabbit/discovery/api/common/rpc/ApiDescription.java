package rabbit.discovery.api.common.rpc;

import rabbit.discovery.api.common.enums.HttpMethod;

public class ApiDescription {

    /**
     * 路径
     */
    private String path;

    /**
     * 类名.方法名
     */
    private String name;

    /**
     * 方法类型
     */
    private HttpMethod method;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }
}
