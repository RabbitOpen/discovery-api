package rabbit.discovery.api.plugins.client.matcher;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.discovery.api.plugins.client.plugin.OpenFeignLoadBalancerPlugin;
import rabbit.discovery.api.plugins.common.matcher.DiscoveryMatcher;
import rabbit.discovery.api.plugins.common.plugin.DiscoveryPlugin;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class OpenFeignLoadBalancerMatcher  extends DiscoveryMatcher {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return named("com.netflix.loadbalancer.BaseLoadBalancer");
    }

    @Override
    public ElementMatcher.Junction methodMatcher(TypeDescription typeDescription) {
        return named("chooseServer");
    }

    @Override
    public DiscoveryPlugin getPlugin() {
        return new OpenFeignLoadBalancerPlugin();
    }
}
