package rabbit.discovery.api.common.rpc;

import rabbit.discovery.api.common.ConfigDetail;
import rabbit.discovery.api.common.PublicKeyDesc;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.common.http.anno.Body;
import rabbit.discovery.api.common.protocol.ApplicationInstance;
import rabbit.discovery.api.common.protocol.PrivilegeData;
import rabbit.discovery.api.common.protocol.RegisterResult;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 与服务侧通信的服务
 */
public interface TcpProtocolService {

    /**
     * 注册自己
     *
     * @param instance
     * @return
     */
    Mono<RegisterResult> register(ApplicationInstance instance);

    /**
     * 维持心跳
     *
     * @param instance
     * @return
     */
    Mono<RegisterResult> keepAlive(ApplicationInstance instance);

    /**
     * 获取应用公钥
     *
     * @param applicationCode
     * @return
     */
    Mono<PublicKeyDesc> getPublicKey(String applicationCode);

    /**
     * 获取注册中心地址
     *
     * @return
     */

    Mono<String> getRegistryAddress();

    /**
     * 获取自己授权出去的权限
     *
     * @param applicationCode
     * @return
     */

    Mono<PrivilegeData> getProviderPrivileges(String applicationCode);

    /**
     * 加载应用的配置
     *
     * @param applicationCode 应用编码
     * @param configFiles     想加载的配置
     * @return
     */
    Mono<ConfigDetail> loadConfig(String applicationCode,
                                  @Body List<RemoteConfig> configFiles);

    /**
     * 上报接口
     * @param applicationCode
     * @param apiData
     * @return
     */
    Mono<Void> doReport(String applicationCode, ApiData apiData);
}
