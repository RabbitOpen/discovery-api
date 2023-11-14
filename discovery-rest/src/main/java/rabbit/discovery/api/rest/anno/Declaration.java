package rabbit.discovery.api.rest.anno;

import rabbit.discovery.api.rest.Policy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static rabbit.discovery.api.rest.Policy.EXCLUDE;

/**
 * api 声明
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Declaration {

    /**
     * 定义的方法
     *
     * @return
     */
    String[] methods() default {};

    /**
     * 声明策略
     *
     * @return
     */
    Policy policy() default EXCLUDE;

    /**
     * context path
     * @return
     */
    String contextPath() default "";
}
