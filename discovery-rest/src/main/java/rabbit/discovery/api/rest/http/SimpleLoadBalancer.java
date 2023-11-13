package rabbit.discovery.api.rest.http;

import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.common.global.ApplicationMetaCache;
import rabbit.discovery.api.common.protocol.ApplicationMeta;
import rabbit.discovery.api.rest.LoadBalancer;
import rabbit.flt.common.utils.StringUtils;

public class SimpleLoadBalancer implements LoadBalancer {

    private Configuration configuration;

    public SimpleLoadBalancer(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public ServerNode choose(HttpRequest request) {
        ApplicationMeta meta = ApplicationMetaCache.getApplicationMeta();
        String application = request.getTargetApplication();
        String clusterName = request.getApplicationCluster();
        if (StringUtils.isEmpty(clusterName)) {
            return meta.getProviderServerNode(application, configuration.getApplicationCluster(application));
        } else {
            return meta.getProviderServerNode(application, clusterName);
        }
    }
}
