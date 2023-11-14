package rabbit.discovery.api.config.plugin;

import rabbit.discovery.api.common.Framework;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.config.loader.ConfigLoaderUtil;
import rabbit.discovery.api.config.loader.SpringMvcConfigLoader;
import rabbit.discovery.api.plugins.common.plugin.DiscoveryPlugin;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

class SpringMvcConfigLoadPlugin extends DiscoveryPlugin {

    @Override
    public Object after(Method method, Object[] args, Object target, Object result) {
        if (Framework.isSpringMvcFrameWork()) {
            SpringMvcConfigLoader configLoader = (SpringMvcConfigLoader) ConfigLoaderUtil.getConfigLoader();
            configLoader.setLocalProperties((Properties) args[0]);
            configLoader.init();
            List<RemoteConfig> configs = configLoader.loadRemoteConfig();
            configLoader.addPropertySources(configs);
            configLoader.start();
        }
        return result;
    }
}
