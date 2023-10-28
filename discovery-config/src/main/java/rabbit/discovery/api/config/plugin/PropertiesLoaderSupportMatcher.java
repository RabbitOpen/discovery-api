package rabbit.discovery.api.config.plugin;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.discovery.api.plugins.common.matcher.DiscoveryMatcher;
import rabbit.discovery.api.plugins.common.plugin.DiscoveryPlugin;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * spring mvc环境下增强PropertiesLoaderSupport，启动 SpringMvcConfigLoader
 */
public class PropertiesLoaderSupportMatcher extends DiscoveryMatcher {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return named("org.springframework.core.io.support.PropertiesLoaderSupport");
    }

    @Override
    public ElementMatcher.Junction methodMatcher(TypeDescription typeDescription) {
        return named("loadProperties");
    }

    @Override
    public DiscoveryPlugin getPlugin() {
        return new SpringMvcConfigLoadPlugin();
    }
}
