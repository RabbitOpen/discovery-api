package rabbit.discovery.api.starter.interceptor;

import rabbit.discovery.api.plugins.common.Plugin;

import java.lang.reflect.Method;

public abstract class MethodInterceptor implements Plugin {

    protected Plugin realPlugin;

    protected Object intercept(Method method, Object[] args, Object target, MethodCallback callback) {
        Object result = null;
        try {
            if (realPlugin.intercept(method, args, target)) {
                result = realPlugin.doIntercept(method, args, target);
            } else {
                result = callback.call(args);
            }
            return realPlugin.after(method, args, target, result);
        } finally {
            realPlugin.doFinally(method, args, target, result);
        }
    }
}
