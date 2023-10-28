package rabbit.discovery.api.plugins.client.plugin;

import feign.RequestTemplate;
import rabbit.discovery.api.common.ApiProtocolHelper;
import rabbit.discovery.api.plugins.common.plugin.DiscoveryPlugin;

import java.lang.reflect.Method;

public class HardCodedTargetPlugin extends DiscoveryPlugin {

    @Override
    public boolean intercept(Method method, Object[] args, Object target) {
        RequestTemplate requestTemplate = (RequestTemplate) args[0];
        if (getConfiguration().isProxyFeign()) {
            ApiProtocolHelper.addProtocolHeader(requestTemplate::header, getConfiguration());
        }
        return false;
    }
}
