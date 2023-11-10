package rabbit.discovery.api.common.rpc;

import rabbit.discovery.api.common.ConfigDetail;
import rabbit.discovery.api.common.PublicKeyDesc;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.common.http.anno.*;
import rabbit.discovery.api.common.protocol.ApplicationInstance;
import rabbit.discovery.api.common.protocol.PrivilegeData;
import rabbit.discovery.api.common.protocol.RegisterResult;

import java.util.List;

/**
 * 与服务侧通信的服务
 */
public interface HttpProtocolService {

    /**
     * 注册自己
     * @param instance
     * @return
     */
    @Header(name = "Content-type", value = "application/json")
    @Post("/discovery/register")
    RegisterResult register(@Body ApplicationInstance instance);

    /**
     * 维持心跳
     * @param instance
     * @return
     */
    @Header(name = "Content-type", value = "application/json")
    @Post("/discovery/keepAlive")
    RegisterResult keepAlive(@Body ApplicationInstance instance);

    /**
     * 获取应用公钥
     * @param applicationCode
     * @return
     */
    @Header(name = "Content-type", value = "application/json")
    @Get("/discovery/getPublicKey/{applicationCode}")
    PublicKeyDesc getPublicKey(@RequestPathVariable("applicationCode") String applicationCode);

    /**
     * 获取注册中心地址
     * @return
     */
    @Header(name = "Content-type", value = "application/json")
    @Get("/discovery/getRegistryAddress")
    String getRegistryAddress();

    /**
     * 获取自己授权出去的权限
     * @param applicationCode
     * @return
     */
    @Header(name = "Content-type", value = "application/json")
    @Post("/discovery/authorizations/provider/{applicationCode}")
    PrivilegeData getProviderPrivileges(@RequestPathVariable("applicationCode") String applicationCode);

    /**
     * 加载应用的配置
     * @param applicationCode  应用编码
     * @param configFiles      想加载的配置
     * @return
     */
    @Header(name = "Content-type", value = "application/json")
    @Post("/discovery/load/{applicationCode}")
    ConfigDetail loadConfig(@RequestPathVariable("applicationCode") String applicationCode,
                            @Body List<RemoteConfig> configFiles);
}