package rabbit.discovery.api.common;

import java.util.ArrayList;
import java.util.List;

public class ConfigDetail {

    private List<RemoteConfig> configs;

    private Long version;

    public ConfigDetail() {
        this(new ArrayList<>(), -1l);
    }

    public ConfigDetail(List<RemoteConfig> configs, Long version) {
        this.configs = configs;
        this.version = version;
    }

    public List<RemoteConfig> getConfigs() {
        return configs;
    }

    public void setConfigs(List<RemoteConfig> configs) {
        this.configs = configs;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
