package rabbit.discovery.api.config.plugin;

import rabbit.discovery.api.common.ConfigDetail;
import rabbit.discovery.api.common.Framework;
import rabbit.discovery.api.config.loader.ConfigLoaderUtil;
import rabbit.discovery.api.config.loader.SpringMvcConfigLoader;
import rabbit.discovery.api.plugins.common.plugin.DiscoveryPlugin;

import java.lang.reflect.Method;
import java.util.Properties;

class SpringMvcConfigLoadPlugin extends DiscoveryPlugin {

    @Override
    public Object after(Method method, Object[] args, Object target, Object result) {
        if (Framework.isSpringMvcFrameWork()) {
            SpringMvcConfigLoader configLoader = (SpringMvcConfigLoader) ConfigLoaderUtil.getConfigLoader();
            configLoader.setLocalProperties((Properties) args[0]);
            configLoader.init();
            ConfigDetail configDetail = configLoader.loadRemoteConfig();
            configLoader.addPropertySources(configDetail.getConfigs());
            configLoader.start();
        }
        return result;
    }
}
