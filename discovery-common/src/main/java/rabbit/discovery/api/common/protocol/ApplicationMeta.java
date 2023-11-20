package rabbit.discovery.api.common.protocol;

import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.exception.LoadBalanceException;
import rabbit.flt.common.utils.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ApplicationMeta {

    /**
     * 授权版本，授权关系变更一次 +1
     */
    private Long privilegeVersion = 0L;

    /**
     * 注册中心地址版本
     */
    private Long registryAddressVersion = 0L;

    /**
     * 配置版本号
     */
    private Long configVersion = 0L;

    /**
     * 白名单客户端
     */
    private Set<String> whiteConsumers = new HashSet<>();

    /**
     * provider的集群实例信息， key是应用编码
     */
    private Map<String, ClusterInstanceMeta> clusterMetas = new ConcurrentHashMap<>();

    /**
     * counter
     */
    private static final Map<String, AtomicLong> cache = new ConcurrentHashMap<>();

    public Set<String> getWhiteConsumers() {
        return whiteConsumers;
    }

    public void setWhiteConsumers(Set<String> whiteConsumers) {
        this.whiteConsumers = whiteConsumers;
    }

    public Long getPrivilegeVersion() {
        return privilegeVersion;
    }

    public void setPrivilegeVersion(Long privilegeVersion) {
        this.privilegeVersion = privilegeVersion;
    }

    public Long getRegistryAddressVersion() {
        return registryAddressVersion;
    }

    public void setRegistryAddressVersion(Long registryAddressVersion) {
        this.registryAddressVersion = registryAddressVersion;
    }

    public Long getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(Long configVersion) {
        this.configVersion = configVersion;
    }

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
        ClusterInstanceMeta clusterMeta = getClusterMetas().get(applicationCode);
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

    public Map<String, ClusterInstanceMeta> getClusterMetas() {
        return clusterMetas;
    }

    public void setClusterMetas(Map<String, ClusterInstanceMeta> clusterMetas) {
        this.clusterMetas = clusterMetas;
    }
}
