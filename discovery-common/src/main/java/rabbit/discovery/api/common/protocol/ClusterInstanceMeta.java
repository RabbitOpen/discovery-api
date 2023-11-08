package rabbit.discovery.api.common.protocol;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClusterInstanceMeta {

    /**
     * 集群的实例列表， key 是集群
     */
    private Map<String, List<ApplicationInstance>> clusterInstMap = new ConcurrentHashMap<>();

    /**
     * 集群的负载地址
     */
    private Map<String, String> clusterLoadBalanceHost = new ConcurrentHashMap<>();

    public Map<String, List<ApplicationInstance>> getClusterInstMap() {
        return clusterInstMap;
    }

    public void setClusterInstMap(Map<String, List<ApplicationInstance>> clusterInstMap) {
        this.clusterInstMap = clusterInstMap;
    }

    public Map<String, String> getClusterLoadBalanceHost() {
        return clusterLoadBalanceHost;
    }

    public void setClusterLoadBalanceHost(Map<String, String> clusterLoadBalanceHost) {
        this.clusterLoadBalanceHost = clusterLoadBalanceHost;
    }

    public void addClusterLoadBalance(String cluster, String balanceHost) {
        getClusterLoadBalanceHost().put(cluster, balanceHost);
    }
}
