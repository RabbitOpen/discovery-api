package rabbit.discovery.api.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.DiscoveryService;
import rabbit.discovery.api.common.TraceConfiguration;
import rabbit.discovery.api.common.spi.SpringMvcPostBeanProcessor;
import rabbit.discovery.api.config.ValueChangeListener;
import rabbit.discovery.api.config.loader.ConfigLoaderUtil;
import rabbit.discovery.api.config.spi.FlexiblePropertyProcessor;
import rabbit.discovery.api.rest.SpringBeanRegistrar;
import rabbit.discovery.api.rest.report.ApiCollector;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * spring mvc 启动器，通过xml导入或者@Import注解导入即可
 * 实现BeanPostProcessor,提升初始化顺序
 */
public class SpringMvcStarter extends MutuallyExclusiveStarter implements BeanPostProcessor {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private Environment environment;

    /**
     * rest client 接口包路径
     */
    private String[] restClientPackages;

    /**
     * open client 接口包路径
     */
    private String[] openApiPackages;

    @Autowired(required = false)
    private ValueChangeListener changeListener;

    private List<SpringMvcPostBeanProcessor> beanProcessors;

    @PostConstruct
    public void init() {
        registerBeans();
        Configuration configuration = getBean(Configuration.class);
        configuration.doValidation();
        ClassUtils.setContext(getContext());
        DiscoveryService discoveryService = getDiscoveryService();
        discoveryService.setConfiguration(configuration);
        discoveryService.start();
        // 注册 open / rest 接口
        registerApiClients();
        beanProcessors = loadProcessors();
    }

    /**
     * 加载后置处理器
     * @return
     */
    private List<SpringMvcPostBeanProcessor> loadProcessors() {
        List<SpringMvcPostBeanProcessor> processors = new ArrayList<>();
        ServiceLoader.load(SpringMvcPostBeanProcessor.class).forEach(processor -> {
            if (processor instanceof FlexiblePropertyProcessor) {
                ((FlexiblePropertyProcessor) processor).setValueChangeListener(changeListener);
            }
            processors.add(processor);
        });
        return processors;
    }

    /**
     * 注册 open / rest 接口
     */
    private void registerApiClients() {
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) getContext();
        Function<String, String> propertyReader = property -> ConfigLoaderUtil.getConfigLoader().readProperty(property);
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        SpringBeanRegistrar registrar = new SpringBeanRegistrar(resourceLoader, environment);
        registrar.registerRestClients(registry, restClientPackages, propertyReader);
        registrar.registerOpenApiClients(registry, openApiPackages, propertyReader);
    }

    /**
     * register spring bean
     */
    private void registerBeans() {
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) getContext();
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        SpringBeanRegistrar registrar = new SpringBeanRegistrar(resourceLoader, environment);
        registrar.registerBeanDefinition(registry, Configuration.class);
        registrar.registerBeanDefinition(registry, TraceConfiguration.class);
        registrar.registerBeanDefinition(registry, ApiCollector.class);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        ApiCollector collector = getBean(ApiCollector.class);
        collector.postProcessBeforeInitialization(bean, beanName);
        for (SpringMvcPostBeanProcessor processor : beanProcessors) {
            // 缓存配置项的meta信息
            processor.before(bean, beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // do nothing
        return bean;
    }

    public void setRestClientPackages(String[] restClientPackages) {
        this.restClientPackages = restClientPackages;
    }

    public void setOpenApiPackages(String[] openApiPackages) {
        this.openApiPackages = openApiPackages;
    }
}
