package rabbit.discovery.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.config.loader.ConfigLoaderUtil;
import rabbit.discovery.api.config.loader.SpringBootConfigLoader;

import java.util.List;

/**
 * 通过spring.factories文件启动
 */
public class SpringBootConfigProcessor implements EnvironmentPostProcessor, BeanPostProcessor {

    @Autowired(required = false)
    private ValueChangeListener valueChangeListener;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        SpringBootConfigLoader configLoader = (SpringBootConfigLoader) ConfigLoaderUtil.getConfigLoader();
        configLoader.setEnvironment(environment);
        configLoader.init();
        // 启动时读取不了配置，抛异常 直接终止
        List<RemoteConfig> configs = configLoader.loadRemoteConfig();
        configLoader.addPropertySources(configs);
        configLoader.start();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        ConfigLoaderUtil.getConfigLoader().injectProperty(bean, valueChangeListener);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
