package rabbit.discovery.api.common;

public interface SpringBeanSupplier {

    /**
     * 获取 spring bean
     * @param clz
     * @param <T>
     * @return
     */
    <T> T getSpringBean(Class<T> clz);

}
