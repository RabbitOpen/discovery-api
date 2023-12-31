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
     * provider的集群实例信息， 一级key是应用编码, 二级key是集群名（编码）
     */
    private Map<String, Map<String, List<ServerNode>>> clusterServerNodes = new ConcurrentHashMap<>();

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
        Map<String, List<ServerNode>> listMap = getClusterServerNodes().get(applicationCode);
        if (null == listMap || !listMap.containsKey(clusterName)) {
            throw new DiscoveryException("获取应用[".concat(applicationCode).concat("]信息失败"));
        }
        List<ServerNode> serverNodes = listMap.get(clusterName);
        if (CollectionUtils.isEmpty(serverNodes)) {
            throw new LoadBalanceException(applicationCode, clusterName);
        }
        return serverNodes.get((int) (getCount(applicationCode) % serverNodes.size()));
    }

    public Map<String, Map<String, List<ServerNode>>> getClusterServerNodes() {
        return clusterServerNodes;
    }

    public void setClusterServerNodes(Map<String, Map<String, List<ServerNode>>> clusterServerNodes) {
        this.clusterServerNodes = clusterServerNodes;
    }
}
