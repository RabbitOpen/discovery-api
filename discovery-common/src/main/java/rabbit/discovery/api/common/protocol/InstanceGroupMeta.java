package rabbit.discovery.api.common.protocol;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InstanceGroupMeta {

    /**
     * 分组的实例列表， key 是分组
     */
    private Map<String, List<ApplicationInstance>> groupInstMap = new ConcurrentHashMap<>();

    /**
     * 分组的负载地址
     */
    private Map<String,String> groupLoadBalanceHost = new ConcurrentHashMap<>();

    public Map<String, List<ApplicationInstance>> getGroupInstMap() {
        return groupInstMap;
    }

    public void setGroupInstMap(Map<String, List<ApplicationInstance>> groupInstMap) {
        this.groupInstMap = groupInstMap;
    }

    public Map<String, String> getGroupLoadBalanceHost() {
        return groupLoadBalanceHost;
    }

    public void setGroupLoadBalanceHost(Map<String, String> groupLoadBalanceHost) {
        this.groupLoadBalanceHost = groupLoadBalanceHost;
    }

    public void addGroupLoadBalance(String group, String balanceHost) {
        getGroupLoadBalanceHost().put(group, balanceHost);
    }
}
