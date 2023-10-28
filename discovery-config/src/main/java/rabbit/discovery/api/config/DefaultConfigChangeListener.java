package rabbit.discovery.api.config;

import rabbit.discovery.api.common.spi.ConfigChangeListener;
import rabbit.discovery.api.config.loader.ConfigLoaderUtil;

public class DefaultConfigChangeListener implements ConfigChangeListener {

    @Override
    public void updateConfig(Long version) {
        ConfigLoaderUtil.getConfigLoader().updateConfig(version);
    }
}
