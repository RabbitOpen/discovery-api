package rabbit.discovery.api.test.spi;

import rabbit.discovery.api.common.ConfigDetail;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.config.loader.SpringMvcConfigLoader;
import rabbit.discovery.api.mvc.ConfigController;

import java.util.List;

public class MySpringMvcConfigLoader extends SpringMvcConfigLoader {

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
    protected final ConfigDetail loadConfigFromServer(String applicationCode, List<RemoteConfig> configFiles) {
        if (null == callback) {
            // 服务还没启动好，加载不了远程配置
            return new ConfigController().loadConfig(applicationCode, configFiles);
        } else {
            return super.loadConfigFromServer(applicationCode, configFiles);
        }
    }

    public static void setCallBack(Runnable callBack) {
        MySpringMvcConfigLoader.callback = callBack;
    }

}
