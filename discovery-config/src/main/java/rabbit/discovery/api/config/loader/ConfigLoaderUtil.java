package rabbit.discovery.api.config.loader;

import rabbit.discovery.api.common.Framework;
import rabbit.discovery.api.config.ConfigLoader;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import static rabbit.discovery.api.common.Framework.SPRING_BOOT;
import static rabbit.discovery.api.common.Framework.SPRING_MVC;

public class ConfigLoaderUtil {

    private static final ConfigLoaderUtil inst = new ConfigLoaderUtil();

    private Map<Framework, ConfigLoader> loaderCache = new ConcurrentHashMap<>();

    private ConfigLoaderUtil() {
    }

    public static ConfigLoader getConfigLoader() {
        return inst.loaderCache.computeIfAbsent(Framework.getFrameWork(), framework -> {
            ConfigLoader loader = inst.createConfigLoader();
            if (null != loader) {
                return loader;
            }
            if (Framework.isSpringMvcFrameWork()) {
                return new SpringMvcConfigLoader();
            } else {
                return new SpringBootConfigLoader();
            }
        });
    }

    private ConfigLoader createConfigLoader() {
        for (ConfigLoader configLoader : ServiceLoader.load(ConfigLoader.class)) {
            if (Framework.isSpringMvcFrameWork()) {
                if (SPRING_MVC == configLoader.getCompatibleFramework()) {
                    return configLoader;
                }
            } else {
                if (SPRING_BOOT == configLoader.getCompatibleFramework()) {
                    return configLoader;
                }
            }
        }
        return null;
    }
}
