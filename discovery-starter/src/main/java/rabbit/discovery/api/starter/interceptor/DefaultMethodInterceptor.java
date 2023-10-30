package rabbit.discovery.api.starter.interceptor;

import net.bytebuddy.implementation.bind.annotation.*;
import rabbit.discovery.api.plugins.common.Plugin;

import java.lang.reflect.Method;

public class DefaultMethodInterceptor extends MethodInterceptor {

    public DefaultMethodInterceptor(Plugin plugin) {
        super.realPlugin = plugin;
    }

    /**
     * 拦截成员方法
     *
     * @param objThis
     * @param method
     * @param args
     * @param callback
     * @return
     */
    @RuntimeType
    public Object interceptMemberMethod(@This Object objThis, @Origin Method method,
                                        @AllArguments Object[] args, @Morph MethodCallback callback) {
        return intercept(method, args, objThis, callback::call);
    }
}
