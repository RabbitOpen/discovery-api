package rabbit.discovery.api.config;

import java.lang.reflect.Field;

public interface ValueChangeListener {

    /**
     * 值变更事件
     * @param bean
     * @param field
     * @param oldValue
     * @param newValue
     */
    void valueChanged(Object bean, Field field, Object oldValue, Object newValue);
}
