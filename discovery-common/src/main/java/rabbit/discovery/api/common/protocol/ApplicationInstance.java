package rabbit.discovery.api.common.protocol;

/**
 * 应用实例
 */
public class ApplicationInstance {

    /**
     * 所属集群名
     */
    private String clusterName;

    /**
     * 实例ip
     */
    private String host;

    /**
     * 实例端口
     */
    private int port;

    /**
     * 应用编码
     */
    private String applicationCode;

    /**
     * 实例id
     */
    private String id;

    public ApplicationInstance(String applicationCode) {
        this();
        this.applicationCode = applicationCode;
    }

    public ApplicationInstance() {
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
