package rabbit.discovery.api.rest;

import org.springframework.context.annotation.Import;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.rest.registrar.SpringBootApiRegistrar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启 rest api 扫描
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({SpringBootApiRegistrar.class, Configuration.class})
public @interface EnableRestClients {

    /**
     * 需要扫描的包路径，默认是引入当前注解的类所处的包
     *
     * @return
     */
    String[] basePackages() default {};
}
