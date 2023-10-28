package rabbit.discovery.api.plugins.common;

import java.lang.reflect.Method;

/**
 * 插件
 */
public interface Plugin {

    /**
     * 方法前置拦截
     * @param method
     * @param args
     * @param target
     * @return  false：继续执行原方法，true不执行原方法，执行 doIntercept
     */
    default boolean intercept(Method method, Object[] args, Object target) {
        // 默认不拦截原方法
        return false;
    }

    /**
     * before 返回 false时执行该方法
     * @param method
     * @param args
     * @param target
     * @return
     */
    default Object doIntercept(Method method, Object[] args, Object target) {
        return null;
    }

    /**
     * 后置拦截
     * @param method
     * @param args
     * @param target
     * @param result
     * @return
     */
    default Object after(Method method, Object[] args, Object target, Object result) {
        return result;
    }

    /**
     * 兜底拦截
     * @param method
     * @param args
     * @param target
     * @param result
     */
    default void doFinally(Method method, Object[] args, Object target, Object result) {
        // ignore
    }
}
