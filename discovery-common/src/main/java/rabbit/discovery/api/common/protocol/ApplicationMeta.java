package rabbit.discovery.api.common.protocol;

import java.util.HashSet;
import java.util.Set;

public class ApplicationMeta {

    /**
     * 服务方
     */
    private Provider provider = new Provider();

    /**
     * 权限版本
     */
    private Long privilegeVersion = 0L;

    /**
     * 授权版本，授权关系变更一次 +1
     */
    private Long authVersion = 0L;

    /**
     * 注册中心地址版本
     */
    private Long registryAddressVersion = 0L;

    /**
     * 配置版本号
     */
    private Long configVersion = -1L;

    /**
     * 白名单客户端
     */
    private Set<String> whiteConsumers = new HashSet<>();

    public Set<String> getWhiteConsumers() {
        return whiteConsumers;
    }

    public void setWhiteConsumers(Set<String> whiteConsumers) {
        this.whiteConsumers = whiteConsumers;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Long getPrivilegeVersion() {
        return privilegeVersion;
    }

    public void setPrivilegeVersion(Long privilegeVersion) {
        this.privilegeVersion = privilegeVersion;
    }

    public Long getAuthVersion() {
        return authVersion;
    }

    public void setAuthVersion(Long authVersion) {
        this.authVersion = authVersion;
    }

    public Long getRegistryAddressVersion() {
        return registryAddressVersion;
    }

    public void setRegistryAddressVersion(Long registryAddressVersion) {
        this.registryAddressVersion = registryAddressVersion;
    }

    public Long getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(Long configVersion) {
        this.configVersion = configVersion;
    }
}
