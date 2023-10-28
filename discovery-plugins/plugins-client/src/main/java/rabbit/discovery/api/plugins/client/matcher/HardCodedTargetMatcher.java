package rabbit.discovery.api.plugins.client.matcher;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.discovery.api.plugins.client.plugin.HardCodedTargetPlugin;
import rabbit.discovery.api.plugins.common.matcher.DiscoveryMatcher;
import rabbit.discovery.api.plugins.common.plugin.DiscoveryPlugin;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class HardCodedTargetMatcher extends DiscoveryMatcher {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return named("feign.Target$HardCodedTarget");
    }

    @Override
    public ElementMatcher.Junction methodMatcher(TypeDescription typeDescription) {
        return named("apply");
    }

    @Override
    public DiscoveryPlugin getPlugin() {
        return new HardCodedTargetPlugin();
    }
}
