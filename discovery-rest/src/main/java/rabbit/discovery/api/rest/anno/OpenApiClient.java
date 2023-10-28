package rabbit.discovery.api.rest.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OpenApiClient {

    /**
     * 凭据
     * 可以是值，也可以是el表达式，例如：${open.credential}
     *
     * @return
     */
    String credential();

    /**
     * 基础uri
     * 可以是值，也可以是el表达式，例如：${open.baseUri}
     *
     * @return
     */
    String baseUri() default "";

    /**
     * 私钥
     * 可以是值，也可以是el表达式，例如：${open.privateKey}
     *
     * @return
     */
    String privateKey();
}
