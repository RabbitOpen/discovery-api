package rabbit.discovery.api.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.DiscoveryService;
import rabbit.discovery.api.common.SpringBeanSupplier;
import rabbit.discovery.api.common.SpringBeanSupplierHolder;
import rabbit.discovery.api.plugins.common.SpringBeanCreator;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.ServiceLoader;

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;

/**
 * spring boot 注册入口， 通过 @DiscoveryAutoConfiguration启动
 * 实现BeanPostProcessor,提升初始化顺序
 */
public class SpringBootStarter extends MutuallyExclusiveStarter implements BeanPostProcessor {

    @Autowired
    private Configuration configuration;

    @PostConstruct
    public void init() {
        registerSpringBean();
        configuration.doValidation();
        ClassUtils.setContext(getContext());
        DiscoveryService discoveryService = getDiscoveryService();
        discoveryService.setConfiguration(configuration);
        discoveryService.start();
    }

    private void registerSpringBean() {
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) getContext();
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        for (SpringBeanCreator bean : ServiceLoader.load(SpringBeanCreator.class)) {
            if (!bean.match()) {
                continue;
            }
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(bean.getBeanClass());
            builder.setScope(BeanDefinition.SCOPE_SINGLETON);
            builder.setAutowireMode(AUTOWIRE_BY_TYPE);
            if (SpringBeanSupplierHolder.class.isAssignableFrom(bean.getBeanClass())) {
                builder.addPropertyValue("supplier", new SpringBeanSupplier() {
                    @Override
                    public <T> T getSpringBean(Class<T> clz) {
                        return getBean(clz);
                    }

                    @Override
                    public <T> Collection<T> getSpringBeans(Class<T> clz) {
                        return getBeans(clz);
                    }
                });
            }
            registry.registerBeanDefinition(bean.getBeanName(), builder.getBeanDefinition());
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        // do nothing
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // do nothing
        return bean;
    }
}
