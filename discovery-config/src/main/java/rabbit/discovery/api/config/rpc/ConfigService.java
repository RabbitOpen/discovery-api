package rabbit.discovery.api.config.rpc;

import rabbit.discovery.api.common.ConfigDetail;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.common.http.anno.*;

import java.util.List;
import java.util.Map;

public interface ConfigService {

    /**
     * 加载应用的配置
     * @param applicationCode  应用编码
     * @param configFiles      想加载的配置
     * @param signatures       请求签名
     * @return
     */
    @Header(name = "Content-type", value = "application/json")
    @Get("/config/load/{applicationCode}")
    ConfigDetail loadConfig(@RequestPathVariable("applicationCode") String applicationCode,
                            @Body List<RemoteConfig> configFiles,
                            @HeaderMap Map<String, String> signatures);
}
