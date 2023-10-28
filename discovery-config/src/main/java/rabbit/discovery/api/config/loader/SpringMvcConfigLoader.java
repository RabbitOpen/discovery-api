package rabbit.discovery.api.config.loader;

import rabbit.discovery.api.common.Framework;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.config.ConfigLoader;
import rabbit.discovery.api.config.PropertyHandler;
import rabbit.discovery.api.config.context.InjectType;
import rabbit.flt.common.utils.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

public class SpringMvcConfigLoader extends ConfigLoader {

    private Properties properties;

    // 最初的配置
    private Properties localProperties;

    @Override
    public Framework getCompatibleFramework() {
        return Framework.SPRING_MVC;
    }

    @Override
    public String readProperty(String propertyName) {
        return StringUtils.toString(properties.get(propertyName));
    }

    @Override
    protected String readProperty(String propertyName, String defaultValue) {
        String value = readProperty(propertyName);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    @Override
    public void addPropertySources(List<RemoteConfig> configs) {
        properties.putAll(getHighPriorityConfigs(configs));
        getLowPriorityConfigs(configs).forEach((k, v) -> properties.computeIfAbsent(k, key -> v));
    }

    @Override
    protected void updatePropertySources(List<RemoteConfig> configs) {
        properties.putAll(getHighPriorityConfigs(configs));
        getLowPriorityConfigs(configs).forEach((k, v) -> {
            if (localProperties.containsKey(k)) {
                // 本地配置不会被低优先级的配置覆盖
                return;
            }
            properties.put(k, v);
        });
        beanMeta.forEach((bean, meta) -> meta.inject(this::readProperty, InjectType.UPDATE));
    }

    /**
     * 获取高优先级的配置
     *
     * @param configs
     * @return
     */
    private Properties getHighPriorityConfigs(List<RemoteConfig> configs) {
        return getConfigsByPriority(configs, priority -> priority < 0);
    }

    /**
     * 获取低优先级的配置
     *
     * @param configs
     * @return
     */
    private Properties getLowPriorityConfigs(List<RemoteConfig> configs) {
        return getConfigsByPriority(configs, priority -> priority >= 0);
    }

    /**
     * 根据优先级获取配置
     *
     * @param configs
     * @param priorityCondition
     * @return
     */
    private Properties getConfigsByPriority(List<RemoteConfig> configs, Function<Integer, Boolean> priorityCondition) {
        Properties result = new Properties();
        PropertyHandler handler = getPropertyHandler();
        configs.sort(Comparator.comparing(RemoteConfig::getPriority));
        configs.forEach(c -> {
            Properties configProperties = readerCache.get(c.getType()).read(c.getContent(), handler);
            if (priorityCondition.apply(c.getPriority())) {
                configProperties.forEach((k, v) -> result.computeIfAbsent(k, key -> v));
            }
        });
        return result;
    }


    public final void setLocalProperties(Properties properties) {
        this.properties = properties;
        localProperties = new Properties();
        localProperties.putAll(properties);
    }
}
