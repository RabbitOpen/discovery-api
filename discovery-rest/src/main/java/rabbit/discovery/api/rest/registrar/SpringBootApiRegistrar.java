package rabbit.discovery.api.rest.registrar;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import rabbit.discovery.api.rest.EnableOpenClients;
import rabbit.discovery.api.rest.EnableRestClients;
import rabbit.discovery.api.rest.SpringBeanRegistrar;
import rabbit.flt.common.utils.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * spring boot环境下通过该类实现 open api / rest api 的自动注册
 */
public class SpringBootApiRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    /**
     * 注册接口定义
     *
     * @param meta
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata meta, BeanDefinitionRegistry registry) {
        SpringBeanRegistrar registrar = new SpringBeanRegistrar(resourceLoader, environment);
        registrar.registerRestClients(registry, resolvePackages4Scan(meta, EnableRestClients.class));
        registrar.registerOpenApiClients(registry, resolvePackages4Scan(meta, EnableOpenClients.class),
                propertyName -> environment.getProperty(propertyName));
    }

    private <T extends Annotation> String[] resolvePackages4Scan(AnnotationMetadata metadata, Class<T> clz) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(clz.getName());
        if (null == attributes) {
            return new String[0];
        }
        String[] basePackages = (String[]) attributes.get("basePackages");
        if (CollectionUtils.isEmpty(basePackages)) {
            String targetClassName = metadata.getClassName();
            if (-1 != targetClassName.indexOf('.')) {
                basePackages = new String[]{targetClassName.substring(0, targetClassName.lastIndexOf('.'))};
            } else {
                basePackages = new String[]{""};
            }
        }
        return basePackages;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


}
