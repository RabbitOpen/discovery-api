package rabbit.discovery.api.rest.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 读超时
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadTimeout {

    /**
     * 读超时，单位ms
     *
     * @return
     */
    int value() default 30000;
}
