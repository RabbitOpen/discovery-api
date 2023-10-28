package rabbit.discovery.api.common.spi;

public interface SpringMvcPostBeanProcessor {

    /**
     * 前置操作
     * @param bean
     * @param name
     */
    void before(Object bean, String name);

    /**
     * 后置操作
     * @param bean
     * @param name
     */
    void after(Object bean, String name);
}
