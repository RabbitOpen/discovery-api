package rabbit.discovery.api.rest.http;

import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.common.global.ApplicationMetaCache;
import rabbit.discovery.api.common.protocol.Provider;
import rabbit.discovery.api.rest.LoadBalancer;
import rabbit.flt.common.utils.StringUtils;

public class SimpleLoadBalancer implements LoadBalancer {

    private Configuration configuration;

    public SimpleLoadBalancer(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public ServerNode choose(String application, String cluster) {
        Provider provider = ApplicationMetaCache.getApplicationMeta().getProvider();
        if (StringUtils.isEmpty(cluster)) {
            return provider.getProviderServerNode(application, configuration.getApplicationCluster(application));
        } else {
            return provider.getProviderServerNode(application, cluster);
        }
    }
}
