package rabbit.discovery.api.config.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态注入的bean，被声明的对象不允许出现循环引用
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FlexibleBean {

    /**
     * 配置的前缀
     * @return
     */
    String propertyPrefix() default "";

    /**
     * 是否允许动态更新
     * @return
     */
    boolean updatable() default true;
}
