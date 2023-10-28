package rabbit.discovery.api.common;

import rabbit.discovery.api.common.enums.ConfigType;

public class RemoteConfig {

    // 应用编码
    private String applicationCode;

    // 命名空间
    private String namespace;

    private String name;

    private String content;

    // 配置类型
    private ConfigType type;

    /**
     * 优先级，值越小，优先级越高
     */
    private Integer priority = 1;

    public String getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ConfigType getType() {
        return type;
    }

    public void setType(ConfigType type) {
        this.type = type;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
