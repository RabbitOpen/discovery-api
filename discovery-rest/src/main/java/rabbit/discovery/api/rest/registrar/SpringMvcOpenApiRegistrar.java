package rabbit.discovery.api.rest.registrar;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.rest.SpringBeanRegistrar;
import rabbit.flt.common.utils.StringUtil;

import java.util.Properties;

/**
 * spring mvc环境下通过该类实现 open api的自动注册
 * 该类可通过@Import注解注入或者xml配置注入
 */
public class SpringMvcOpenApiRegistrar extends PropertyResourceConfigurer implements ResourceLoaderAware,
        EnvironmentAware, ApplicationContextAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    private Properties properties;

    private ConfigurableApplicationContext applicationContext;

    /**
     * open api 包路径
     */
    private String[] openApiPackages;

    /**
     * 获取全局配置
     *
     * @param configurableListableBeanFactory
     * @param properties
     * @throws BeansException
     */
    @Override
    protected void processProperties(ConfigurableListableBeanFactory configurableListableBeanFactory,
                                     Properties properties) {
        this.properties = properties;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        init((ConfigurableApplicationContext) context);
    }

    /**
     * 初始化（幂等）
     * @param context
     */
    private synchronized void init(ConfigurableApplicationContext context) {
        if (null != this.applicationContext) {
            return;
        }
        this.applicationContext = context;
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        SpringBeanRegistrar registrar = new SpringBeanRegistrar(resourceLoader, environment);
        // 声明Configuration
        registrar.registerBeanDefinition(registry, Configuration.class);
        registrar.registerOpenApiClients(registry, openApiPackages, property -> properties.containsKey(property)
                ? StringUtil.toString(properties.containsKey(property)) : property);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * spring 注入时调用
     *
     * @param openApiPackages
     */
    public void setOpenApiPackages(String[] openApiPackages) {
        this.openApiPackages = openApiPackages;
    }

}
