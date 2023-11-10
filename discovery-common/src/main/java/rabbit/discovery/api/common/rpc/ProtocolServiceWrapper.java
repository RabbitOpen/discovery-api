package rabbit.discovery.api.common.rpc;

import rabbit.discovery.api.common.*;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.protocol.ApplicationInstance;
import rabbit.discovery.api.common.protocol.PrivilegeData;
import rabbit.discovery.api.common.protocol.RegisterResult;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 协议代理对象
 */
public class ProtocolServiceWrapper {

    private static final ProtocolServiceWrapper inst = new ProtocolServiceWrapper();

    private Configuration configuration;

    // 通信协议对象
    private Object protocolService;

    /**
     * 方法缓存
     */
    private Map<String, Method> methodCache = new HashMap<>();

    private ProtocolServiceWrapper() {
    }

    public static RegisterResult register(ApplicationInstance instance) {
        return inst.callMethod("register", instance);
    }

    public static RegisterResult keepAlive(ApplicationInstance instance) {
        return inst.callMethod("keepAlive", instance);
    }

    public static PublicKeyDesc getPublicKey(String applicationCode) {
        return inst.callMethod("getPublicKey", applicationCode);
    }

    public static ConfigDetail loadConfig(String applicationCode, List<RemoteConfig> configFiles) {
        return inst.callMethod("loadConfig", applicationCode, configFiles);
    }

    public static String getRegistryAddress() {
        return inst.callMethod("getRegistryAddress");
    }

    /**
     * 获取授权明细
     * @param applicationCode
     * @return
     */
    public static PrivilegeData getProviderPrivileges(String applicationCode) {
        return inst.callMethod("getProviderPrivileges", applicationCode);
    }

    /**
     * 上报接口
     * @param applicationCode
     * @param apiData
     */
    public static void doReport(String applicationCode, ApiData apiData) {
        inst.callMethod("doReport", applicationCode, apiData);
    }

    /**
     * 初始化代理对象
     *
     * @param configuration
     */
    public static synchronized void init(Configuration configuration) {
        if (null != inst.configuration) {
            return;
        }
        inst.configuration = configuration;
        if (CommunicationMode.TCP == configuration.getCommunicationMode()) {
            inst.protocolService = RpcFactory.proxy(HttpProtocolService.class, configuration);
        } else {
            inst.protocolService = RequestFactory.proxy(HttpProtocolService.class, configuration);
        }
        for (Method method : HttpProtocolService.class.getDeclaredMethods()) {
            inst.methodCache.put(method.getName(), method);
        }
    }

    /**
     * 调用方法
     *
     * @param name
     * @param args
     * @return
     */
    private <T> T callMethod(String name, Object... args) {
        try {
            return (T) inst.methodCache.get(name).invoke(inst.protocolService, args);
        } catch (Exception e) {
            if (null != e.getCause()) {
                throw new DiscoveryException(e.getCause());
            } else {
                throw new DiscoveryException(e.getMessage());
            }
        }
    }

}
