package rabbit.discovery.api.common.rpc;

import rabbit.discovery.api.common.ConfigDetail;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.common.http.anno.Body;
import rabbit.discovery.api.common.http.anno.Header;
import rabbit.discovery.api.common.http.anno.Post;
import rabbit.discovery.api.common.http.anno.RequestPathVariable;

import java.util.List;

public interface ConfigService {

    /**
     * 加载应用的配置
     * @param applicationCode  应用编码
     * @param configFiles      想加载的配置
     * @return
     */
    @Header(name = "Content-type", value = "application/json")
    @Post("/config/load/{applicationCode}")
    ConfigDetail loadConfig(@RequestPathVariable("applicationCode") String applicationCode,
                            @Body List<RemoteConfig> configFiles);
}
