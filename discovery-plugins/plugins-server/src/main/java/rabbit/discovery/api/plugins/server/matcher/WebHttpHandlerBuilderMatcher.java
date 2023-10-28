package rabbit.discovery.api.plugins.server.matcher;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import rabbit.discovery.api.plugins.common.matcher.DiscoveryMatcher;
import rabbit.discovery.api.plugins.common.plugin.DiscoveryPlugin;
import rabbit.discovery.api.plugins.server.plugin.WebFluxFilterPlugin;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class WebHttpHandlerBuilderMatcher extends DiscoveryMatcher {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return named("org.springframework.web.server.adapter.WebHttpHandlerBuilder");
    }

    @Override
    public ElementMatcher.Junction methodMatcher(TypeDescription typeDescription) {
        return isPublic().and(named("build")).and(takesArguments(0));
    }

    @Override
    public DiscoveryPlugin getPlugin() {
        return new WebFluxFilterPlugin();
    }
}
