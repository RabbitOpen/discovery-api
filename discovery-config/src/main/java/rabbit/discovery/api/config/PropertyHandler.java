package rabbit.discovery.api.config;

/**
 * 属性处理拦截
 */
public interface PropertyHandler {

    String handle(String key, String value);
}
