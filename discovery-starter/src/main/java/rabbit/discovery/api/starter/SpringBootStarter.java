package rabbit.discovery.api.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import rabbit.discovery.api.common.Configuration;

/**
 * spring boot 注册入口， 通过 @DiscoveryAutoConfiguration启动
 */
public class SpringBootStarter extends MutuallyExclusiveStarter implements BeanPostProcessor {

    @Autowired
    private Configuration configuration;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
