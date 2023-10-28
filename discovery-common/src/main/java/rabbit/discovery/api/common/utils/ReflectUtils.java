package rabbit.discovery.api.common.utils;

import rabbit.discovery.api.common.exception.ConfigException;

import java.lang.reflect.Field;

public class ReflectUtils {

    private ReflectUtils() {
    }

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

    /**
     * 加载指定class
     * @param clzName
     * @return
     */
    public static <T> T loadClass(String clzName) {
        try {
            return (T) Class.forName(clzName);
        } catch (Exception e) {
            throw new ConfigException(e);
        }
    }

    /**
     * 是否包含指定class
     *
     * @param clzName
     * @return
     */
    public static boolean hasClass(String clzName) {
        try {
            Class.forName(clzName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
