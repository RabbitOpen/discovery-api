package rabbit.discovery.api.plugins.common.plugin;

import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.common.SpringBeanSupplierHolder;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.global.ApplicationMetaCache;
import rabbit.discovery.api.common.protocol.ApplicationMeta;
import rabbit.discovery.api.plugins.common.Plugin;

public abstract class DiscoveryPlugin extends SpringBeanSupplierHolder implements Plugin {

    /**
     * 获取全局配置
     * @return
     */
    protected final Configuration getConfiguration() {
        if (null != getSupplier()) {
            return getSupplier().getSpringBean(Configuration.class);
        }
        throw new DiscoveryException("no configuration supplier exception");
    }

    /**
     * 获取服务节点
     * @param providerAppCode
     * @return
     */
    protected ServerNode getProviderNode(String providerAppCode) {
        Configuration configuration = getConfiguration();
        String cluster = configuration.getApplicationCluster(providerAppCode);
        ApplicationMeta meta = ApplicationMetaCache.getApplicationMeta();
        return meta.getProviderServerNode(providerAppCode, cluster);
    }
}
