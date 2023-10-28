package rabbit.discovery.api.plugins.client.matcher;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.discovery.api.plugins.client.plugin.SpringCloudLoadBalancerPlugin;
import rabbit.discovery.api.plugins.common.matcher.DiscoveryMatcher;
import rabbit.discovery.api.plugins.common.plugin.DiscoveryPlugin;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

public class SpringCloudLoadBalancerMatcher extends DiscoveryMatcher {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return named("org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient");
    }

    @Override
    public ElementMatcher.Junction methodMatcher(TypeDescription typeDescription) {
        return named("choose").and(takesArguments(2));
    }

    @Override
    public DiscoveryPlugin getPlugin() {
        return new SpringCloudLoadBalancerPlugin();
    }
}
