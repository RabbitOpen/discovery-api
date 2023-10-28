package rabbit.discovery.api.plugins.common;

public interface SpringBeanCreator {

    /**
     * bean名字
     * @return
     */
    String getBeanName();

    /**
     * bean所属的类
     * @return
     */
    Class<?> getBeanClass();

    /**
     * 满足条件则创建bean
     * @return
     */
    default boolean match() {
        return true;
    }
}
