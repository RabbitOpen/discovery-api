package rabbit.discovery.api.common.rpc;

import java.util.List;

public class ApiData {

    private String className;

    private List<ApiDescription> apiList;

    public ApiData() {
    }

    public ApiData(String className, List<ApiDescription> apiList) {
        this();
        this.setClassName(className);
        this.setApiList(apiList);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<ApiDescription> getApiList() {
        return apiList;
    }

    public void setApiList(List<ApiDescription> apiList) {
        this.apiList = apiList;
    }
}
