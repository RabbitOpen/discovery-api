package rabbit.discovery.api.test.spi;

import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.config.loader.SpringBootConfigLoader;
import rabbit.discovery.api.test.controller.DiscoveryController;

import java.util.List;

public class MySpringBootConfigLoader extends SpringBootConfigLoader {

    private static Runnable callback;

    @Override
    protected void updatePropertySources(List<RemoteConfig> configs) {
        super.updatePropertySources(configs);
        // 配置更新了
        if (null != callback) {
            callback.run();
        }
    }

    /**
     * 适配加载远端配置
     * @param applicationCode
     * @param configFiles
     * @return
     */
    @Override
    protected final List<RemoteConfig> loadConfigFromServer(String applicationCode, List<RemoteConfig> configFiles) {
        if (null == callback) {
            // 服务还没启动好，加载不了远程配置
            return new DiscoveryController().loadConfig(applicationCode, configFiles);
        } else {
            return super.loadConfigFromServer(applicationCode, configFiles);
        }
    }

    public static void setCallBack(Runnable callBack) {
        MySpringBootConfigLoader.callback = callBack;
    }

}
