package rabbit.discovery.api.rest.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestClient {

    /**
     * 服务方应用
     *
     * @return
     */
    String application();

    /**
     * contextPath
     *
     * @return
     */
    String contextPath() default "";

}
