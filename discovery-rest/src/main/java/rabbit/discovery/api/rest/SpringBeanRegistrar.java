package rabbit.discovery.api.rest;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.util.MultiValueMap;
import rabbit.discovery.api.rest.anno.OpenApiClient;
import rabbit.discovery.api.rest.anno.RestClient;
import rabbit.discovery.api.rest.facotry.OpenClientFactory;
import rabbit.discovery.api.rest.facotry.RestClientFactory;
import rabbit.discovery.api.rest.http.OpenApiExecutor;
import rabbit.discovery.api.rest.http.RestClientExecutor;
import rabbit.flt.common.utils.CollectionUtil;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Function;

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * 注册工具类
 */
public class SpringBeanRegistrar {

    private ResourceLoader resourceLoader;

    private Environment environment;

    public SpringBeanRegistrar(ResourceLoader resourceLoader, Environment environment) {
        this.resourceLoader = resourceLoader;
        this.environment = environment;
    }

    /**
     * 注册rest client spring bean
     *
     * @param registry
     * @param basePackages
     */
    public void registerRestClients(BeanDefinitionRegistry registry, String[] basePackages) {
        if (CollectionUtil.isEmpty(basePackages)) {
            return;
        }
        registerExecutorDefinition(registry, RestClientExecutor.class);
        ClassPathScanningCandidateComponentProvider scanner = createScanner(basePackages, RestClient.class);
        for (String basePackage : basePackages) {
            Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition definition : beanDefinitions) {
                registerRestClientBeanDefinition(registry, definition);
            }
        }
    }

    /**
     * 注册open api spring bean
     * @param registry
     * @param basePackages
     * @param propertyReader
     */
    public void registerOpenApiClients(BeanDefinitionRegistry registry, String[] basePackages, Function<String, String> propertyReader) {
        if (CollectionUtil.isEmpty(basePackages)) {
            return;
        }
        registerExecutorDefinition(registry, OpenApiExecutor.class);
        ClassPathScanningCandidateComponentProvider scanner = createScanner(basePackages, OpenApiClient.class);
        for (String basePackage : basePackages) {
            Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition definition : beanDefinitions) {
                registerOpenApiBeanDefinition(registry, definition, propertyReader);
            }
        }
    }

    private void registerRestClientBeanDefinition(BeanDefinitionRegistry registry, BeanDefinition definition) {
        if (!(definition instanceof AnnotatedBeanDefinition)) {
            return;
        }
        AnnotationMetadata meta = ((AnnotatedBeanDefinition) definition).getMetadata();
        MultiValueMap<String, Object> attributes = meta.getAllAnnotationAttributes(RestClient.class.getName());
        String beanName = meta.getClassName();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RestClientFactory.class);
        builder.setScope(SCOPE_SINGLETON);
        builder.setAutowireMode(AUTOWIRE_BY_TYPE);
        builder.addPropertyValue("application", attributes.get("application"));
        builder.addConstructorArgValue(meta.getClassName());
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    private void registerOpenApiBeanDefinition(BeanDefinitionRegistry registry, BeanDefinition definition,  Function<String, String> propertyReader) {
        if (!(definition instanceof AnnotatedBeanDefinition)) {
            return;
        }
        AnnotationMetadata meta = ((AnnotatedBeanDefinition) definition).getMetadata();
        MultiValueMap<String, Object> attributes = meta.getAllAnnotationAttributes(OpenApiClient.class.getName());
        String beanName = meta.getClassName();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(OpenClientFactory.class);
        builder.setScope(SCOPE_SINGLETON);
        builder.setAutowireMode(AUTOWIRE_BY_TYPE);
        builder.addPropertyValue("credential", attributes.get("credential"));
        builder.addPropertyValue("baseUri", attributes.get("baseUri"));
        builder.addPropertyValue("privateKey", attributes.get("privateKey"));
        builder.addPropertyValue("propertyReader", propertyReader);
        builder.addConstructorArgValue(meta.getClassName());
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    /**
     * 注册执行器bean定义
     *
     * @param registry
     * @param beanClass
     * @param <T>
     */
    private <T extends HttpRequestExecutor> void registerExecutorDefinition(BeanDefinitionRegistry registry, Class<T> beanClass) {
        registerBeanDefinition(registry, beanClass);
    }

    /**
     * 注册spring bean
     *
     * @param registry
     * @param beanClass
     * @param <T>
     */
    public <T> void registerBeanDefinition(BeanDefinitionRegistry registry, Class<T> beanClass) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        builder.setScope(SCOPE_SINGLETON);
        builder.setAutowireMode(AUTOWIRE_BY_TYPE);
        String name = beanClass.getSimpleName();
        name = name.substring(0, 1).toLowerCase().concat(name.length() > 1 ? name.substring(1) : "");
        registry.registerBeanDefinition(name, builder.getBeanDefinition());
    }

    /**
     * 创建特定class类的扫描对象
     *
     * @param basePackages
     * @param clz
     * @param <T>
     * @return
     */
    private <T extends Annotation> ClassPathScanningCandidateComponentProvider createScanner(String[] basePackages, Class<T> clz) {
        ClassPathScanningCandidateComponentProvider scanner = createScanner(clz);
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            ClassMetadata metadata = metadataReader.getClassMetadata();
            for (String pkg : basePackages) {
                if (metadata.getClassName().startsWith(pkg)) {
                    // 指定包下的接口才扫描
                    return true;
                }
            }
            return false;
        });
        return scanner;
    }

    private <T extends Annotation> ClassPathScanningCandidateComponentProvider createScanner(Class<T> clz) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false, environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition definition) {
                // 是接口，不是内部类，不是注解，被@RestClient标记
                AnnotationMetadata meta = definition.getMetadata();
                return meta.isIndependent() && meta.isInterface() && meta.isAnnotated(clz.getName());
            }
        };
        scanner.setResourceLoader(resourceLoader);
        return scanner;
    }
}
