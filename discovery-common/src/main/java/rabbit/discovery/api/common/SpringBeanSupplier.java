package rabbit.discovery.api.common;

import java.util.ArrayList;
import java.util.Collection;

public interface SpringBeanSupplier {

    /**
     * 获取 spring bean
     * @param clz
     * @param <T>
     * @return
     */
    <T> T getSpringBean(Class<T> clz);

    /**
     * 获取指定的spring bean
     * @param clz
     * @param <T>
     * @return
     */
    default <T> Collection<T> getSpringBeans(Class<T> clz) {
        return new ArrayList<>();
    }
}
