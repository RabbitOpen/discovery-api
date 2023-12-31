package rabbit.discovery.api.rest.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开放接口编码
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OpenApiCode {

    /**
     * 接口编码
     *
     * @return
     */
    String value();
}
