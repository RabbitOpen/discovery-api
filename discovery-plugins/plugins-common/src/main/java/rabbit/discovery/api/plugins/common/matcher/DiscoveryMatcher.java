package rabbit.discovery.api.plugins.common.matcher;

import rabbit.discovery.api.plugins.common.Matcher;
import rabbit.discovery.api.plugins.common.plugin.DiscoveryPlugin;

public abstract class DiscoveryMatcher implements Matcher {

    @Override
    public abstract DiscoveryPlugin getPlugin();
}
