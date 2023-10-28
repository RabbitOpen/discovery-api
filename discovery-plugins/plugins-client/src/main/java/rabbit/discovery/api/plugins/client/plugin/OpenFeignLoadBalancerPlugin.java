package rabbit.discovery.api.plugins.client.plugin;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.plugins.common.plugin.DiscoveryPlugin;

import java.lang.reflect.Method;

public class OpenFeignLoadBalancerPlugin extends DiscoveryPlugin {

    @Override
    public boolean intercept(Method method, Object[] args, Object target) {
        // 需要代理就拦截
        return getConfiguration().isProxyFeign();
    }

    @Override
    public Object doIntercept(Method method, Object[] args, Object target) {
        BaseLoadBalancer balancer = (BaseLoadBalancer) target;
        ServerNode node = getProviderNode(balancer.getName());
        return new Server(node.getSchema().name().toLowerCase(), node.getHost(), node.getPort());
    }
}
