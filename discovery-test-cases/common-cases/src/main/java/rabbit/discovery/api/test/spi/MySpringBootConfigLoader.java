package rabbit.discovery.api.test.spi;

import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.config.loader.SpringBootConfigLoader;

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

    public static void setCallBack(Runnable callBack) {
        MySpringBootConfigLoader.callback = callBack;
    }
}
