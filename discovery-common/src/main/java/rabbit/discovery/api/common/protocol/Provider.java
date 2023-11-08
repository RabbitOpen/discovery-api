package rabbit.discovery.api.common.protocol;

import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.exception.LoadBalanceException;
import rabbit.flt.common.utils.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Provider {

    /**
     * provider的集群实例信息， key是应用编码
     */
    private Map<String, ClusterInstanceMeta> instanceGroupMetas = new ConcurrentHashMap<>();

    /**
     * counter
     */
    private static final Map<String, AtomicLong> cache = new ConcurrentHashMap<>();

    private long getCount(String applicationCode) {
        return cache.computeIfAbsent(applicationCode, code -> new AtomicLong(0)).incrementAndGet();
    }

    /**
     * 获取实例
     * @param applicationCode   provider的应用编码
     * @param clusterName       集群名
     * @return
     */
    public ServerNode getProviderServerNode(String applicationCode, String clusterName) {
        ClusterInstanceMeta clusterMeta = getInstanceGroupMetas().get(applicationCode);
        if (null == clusterMeta) {
            throw new DiscoveryException("获取应用[".concat(applicationCode).concat("]信息失败"));
        }
        if (clusterMeta.getClusterLoadBalanceHost().containsKey(clusterName)) {
            return new ServerNode(clusterMeta.getClusterLoadBalanceHost().get(clusterName));
        } else {
            List<ApplicationInstance> clusterInstances = clusterMeta.getClusterInstMap().get(clusterName);
            if (CollectionUtils.isEmpty(clusterInstances)) {
                throw new LoadBalanceException(applicationCode, clusterName);
            }
            int index = (int) (getCount(applicationCode) % clusterInstances.size());
            ApplicationInstance instance = clusterInstances.get(index);
            return new ServerNode(instance.getHost(), instance.getPort());
        }
    }

    public Map<String, ClusterInstanceMeta> getInstanceGroupMetas() {
        return instanceGroupMetas;
    }

    public void setInstanceGroupMetas(Map<String, ClusterInstanceMeta> instanceGroupMetas) {
        this.instanceGroupMetas = instanceGroupMetas;
    }
}
