package rabbit.discovery.api.config.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态更新的字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FlexibleValue {

    /**
     * 配置字段
     * @return
     */
    String value();

    /**
     * 配置是json字段
     * @return
     */
    boolean json() default false;
}
