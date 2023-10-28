package rabbit.discovery.api.common.utils;

import rabbit.discovery.api.common.exception.ConfigException;

import java.lang.reflect.Field;

public class ReflectUtils {

    private ReflectUtils() {}

    public static void setValue(Object bean, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(bean, value);
        } catch (Exception e) {
            throw new ConfigException(e);
        }
    }

    public static <T> T newInstance(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ConfigException(e);
        }
    }
}
