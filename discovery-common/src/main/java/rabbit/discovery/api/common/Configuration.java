package rabbit.discovery.api.common;

import org.springframework.beans.factory.annotation.Value;
import rabbit.discovery.api.common.enums.HttpMode;
import rabbit.discovery.api.common.enums.SecurityMode;
import rabbit.discovery.api.common.exception.DiscoveryException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static rabbit.discovery.api.common.CommunicationMode.TCP;
import static rabbit.flt.common.utils.StringUtils.isEmpty;

public class Configuration {

    private static final int UNDEFINED_PORT = -1;

    /**
     * 注册中心地址
     */
    @Value("${discovery.registry.address:}")
    private String registryAddress;

    /**
     * 应用编码
     */
    @Value("${discovery.application.code:}")
    private String applicationCode;

    /**
     * 应用集群
     */
    @Value("${discovery.application.clusterName:default}")
    private String clusterName = "default";

    /**
     * 用户强制指定的服务端口（默认从serverPort字段读取，如果设置了该字段则忽略serverPort）
     */
    @Value("${discovery.application.port:-1}")
    private int port;

    /**
     * 上报的地址
     */
    @Value("${discovery.application.host:}")
    private String host;

    /**
     * 默认的服务端口
     */
    @Value("${server.port:-1}")
    private int serverPort;

    /**
     * 上下文
     */
    @Value("${discovery.application.context-path:}")
    private String contextPath;

    /**
     * 名单模式，默认白名单
     */
    @Value("${discovery.application.security.mode:WHITE}")
    private SecurityMode mode;

    /**
     * url
     */
    @Value("${discovery.application.security.patterns:/**}")
    private String patterns;

    /**
     * 密钥
     */
    @Value("${discovery.application.security.key:}")
    private String privateKey;

    /**
     * 重放窗口
     */
    @Value("${discovery.application.security.replayWindow:30}")
    private int replayWindow;

    /**
     * 格式 {app1}:{cluster1},{app2}:{cluster2}
     */
    @Value("${discovery.provider.applicationClusters:}")
    private String applicationClusters;

    /**
     * 代理feign
     */
    @Value("${discovery.feign.proxy:true}")
    private boolean proxyFeign;

    /**
     * http client 模型，同步 / 异步
     */
    @Value("${discovery.application.http.mode:SYNC}")
    private HttpMode httpMode;

    /**
     * 连接超时，单位毫秒
     */
    @Value("${discovery.application.http.connection-timeout:5000}")
    private int connectionTimeout;

    /**
     * 读超时，单位毫秒
     */
    @Value("${discovery.application.http.read-timeout:30000}")
    private int readTimeout;

    /**
     * 最大连接数
     */
    @Value("${discovery.application.http.max-connection:200}")
    private int maxConnection;

    /**
     * 每个服务方的最大连接数，异步框架下无效
     */
    @Value("${discovery.application.http.max-connection-per-host:20}")
    private int maxConnectionPerHost;

    /**
     * 通信模式
     */
    @Value("${discovery.communication.mode:TCP}")
    private CommunicationMode communicationMode = TCP;

    private List<String> serverList = new ArrayList<>();

    private long serverIndex = 0L;

    private Map<String, String> applicationClusterMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        this.serverList = resolveServerList(getRegistryAddress());
        if (isEmpty(applicationClusters)) {
            return;
        }
        for (String cluster : applicationClusters.trim().split(",")) {
            String[] kv = cluster.split(":");
            if (2 != kv.length) {
                continue;
            }
            applicationClusterMap.put(kv[0].trim(), kv[1].trim());
        }
    }

    private List<String> resolveServerList(String registryAddress) {
        List<String> list = new ArrayList<>();
        if (isEmpty(registryAddress)) {
            return list;
        }
        for (String address : registryAddress.split(",")) {
            address = address.trim();
            while (address.endsWith("/")) {
                address = address.substring(0, address.length() - 1);
            }
            if (!address.startsWith("http://")) {
                address = "http://".concat(address);
            }
            if (!list.contains(address)) {
                list.add(address);
            }
        }
        return list;
    }

    /**
     * 获取指定应用的集群（消费时）
     *
     * @param applicationCode
     * @return
     */
    public String getApplicationCluster(String applicationCode) {
        return applicationClusterMap.computeIfAbsent(applicationCode, app -> "default");
    }

    public String nextRegistryAddress() {
        return serverList.get((int) (Math.abs(serverIndex++) % serverList.size()));
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        if (!Objects.equals(registryAddress, this.registryAddress)) {
            this.serverList = resolveServerList(registryAddress);
        }
        this.registryAddress = registryAddress;
    }

    public String getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName.trim();
    }

    /**
     * 优先使用自定义的端口
     *
     * @return
     */
    public int getPort() {
        if (UNDEFINED_PORT != port) {
            return port;
        }
        return serverPort;
    }

    /**
     * 校验
     */
    public void doValidation() {
        if (isEmpty(applicationCode)) {
            throw new DiscoveryException("应用编码信息不能为空");
        }
        if (isEmpty(registryAddress)) {
            throw new DiscoveryException("注册中心地址信息不能为空");
        }
        if (isEmpty(privateKey)) {
            throw new DiscoveryException("应用密钥信息不能为空");
        }
        if (UNDEFINED_PORT == getPort()) {
            throw new DiscoveryException("应用端口信息不能为空");
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public SecurityMode getMode() {
        return mode;
    }

    public void setMode(SecurityMode mode) {
        this.mode = mode;
    }

    public String getPatterns() {
        return patterns;
    }

    public void setPatterns(String patterns) {
        this.patterns = patterns;
    }

    public String getPrivateKey() {
        return null == privateKey ? null : privateKey.trim();
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public int getReplayWindow() {
        return replayWindow;
    }

    public void setReplayWindow(int replayWindow) {
        this.replayWindow = replayWindow;
    }

    public String getApplicationClusters() {
        return applicationClusters;
    }

    public void setApplicationClusters(String applicationClusters) {
        this.applicationClusters = applicationClusters;
    }

    public boolean isProxyFeign() {
        return proxyFeign;
    }

    public void setProxyFeign(boolean proxyFeign) {
        this.proxyFeign = proxyFeign;
    }

    public HttpMode getHttpMode() {
        return httpMode;
    }

    public void setHttpMode(HttpMode httpMode) {
        this.httpMode = httpMode;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    public int getMaxConnectionPerHost() {
        return maxConnectionPerHost;
    }

    public void setMaxConnectionPerHost(int maxConnectionPerHost) {
        this.maxConnectionPerHost = maxConnectionPerHost;
    }

    public Map<String, String> getApplicationClusterMap() {
        return applicationClusterMap;
    }

    public void setApplicationClusterMap(Map<String, String> applicationClusterMap) {
        this.applicationClusterMap = applicationClusterMap;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public CommunicationMode getCommunicationMode() {
        return communicationMode;
    }

    public void setCommunicationMode(CommunicationMode communicationMode) {
        this.communicationMode = communicationMode;
    }
}
