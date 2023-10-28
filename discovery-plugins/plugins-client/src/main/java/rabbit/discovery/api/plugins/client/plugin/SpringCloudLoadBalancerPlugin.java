package rabbit.discovery.api.plugins.client.plugin;

import org.springframework.cloud.client.DefaultServiceInstance;
import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.plugins.common.plugin.DiscoveryPlugin;

import java.lang.reflect.Method;

public class SpringCloudLoadBalancerPlugin extends DiscoveryPlugin {

    @Override
    public boolean intercept(Method method, Object[] args, Object target) {
        // 需要代理就拦截
        return getConfiguration().isProxyFeign();
    }

    @Override
    public Object doIntercept(Method method, Object[] args, Object target) {
        String providerApp = (String) args[0];
        ServerNode node = getProviderNode(providerApp);
        return new DefaultServiceInstance("0", providerApp, node.getHost(), node.getPort(),
                node.isHttps());
    }
}
