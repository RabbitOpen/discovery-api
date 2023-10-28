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
     * provider的实例组信息
     */
    private Map<String, InstanceGroupMeta> instanceGroupMetas = new ConcurrentHashMap<>();

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
     * @param groupName         分组名
     * @return
     */
    public ServerNode getProviderServerNode(String applicationCode, String groupName) {
        InstanceGroupMeta groupMeta = getInstanceGroupMetas().get(applicationCode);
        if (null == groupMeta) {
            throw new DiscoveryException("获取应用[".concat(applicationCode).concat("]信息失败"));
        }
        if (groupMeta.getGroupLoadBalanceHost().containsKey(groupName)) {
            return new ServerNode(groupMeta.getGroupLoadBalanceHost().get(groupName));
        } else {
            List<ApplicationInstance> groupInstances = groupMeta.getGroupInstMap().get(groupName);
            if (CollectionUtils.isEmpty(groupInstances)) {
                throw new LoadBalanceException(applicationCode, groupName);
            }
            int index = (int) (getCount(applicationCode) % groupInstances.size());
            ApplicationInstance instance = groupInstances.get(index);
            return new ServerNode(instance.getHost(), instance.getPort());
        }
    }

    public Map<String, InstanceGroupMeta> getInstanceGroupMetas() {
        return instanceGroupMetas;
    }

    public void setInstanceGroupMetas(Map<String, InstanceGroupMeta> instanceGroupMetas) {
        this.instanceGroupMetas = instanceGroupMetas;
    }
}
