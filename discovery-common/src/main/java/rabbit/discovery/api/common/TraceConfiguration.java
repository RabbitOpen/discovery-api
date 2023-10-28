package rabbit.discovery.api.common;

import org.springframework.beans.factory.annotation.Value;
import rabbit.flt.common.AgentConfig;

public class TraceConfiguration extends AgentConfig {

    /**
     * 禁用全链路追踪
     */
    private boolean fltDisabled = false;

    /**
     * 配置服务器
     * @param servers
     */
    @Value("${discovery.application.flt.servers:}")
    @Override
    public void setServers(String servers) {
        super.setServers(servers);
    }

    @Value("${discovery.application.code:}")
    @Override
    public void setApplicationCode(String applicationCode) {
        super.setApplicationCode(applicationCode);
    }

    @Value("${discovery.application.flt.metricsOnly:false}")
    @Override
    public void setMetricsOnly(boolean metricsOnly) {
        super.setMetricsOnly(metricsOnly);
    }

    @Value("${discovery.application.flt.diskSpaceMetricsDirs:}")
    @Override
    public void setDiskSpaceMetricsDirs(String diskSpaceMetricsDirs) {
        super.setDiskSpaceMetricsDirs(diskSpaceMetricsDirs);
    }

    /**
     * 设置网卡
     * @param netMetricsCards
     */
    @Value("${discovery.application.flt.netMetricsCards:eth0}")
    @Override
    public void setNetMetricsCards(String netMetricsCards) {
        super.setNetMetricsCards(netMetricsCards);
    }

    @Value("${discovery.application.flt.gcMetricsEnabled:true}")
    @Override
    public void setGcMetricsEnabled(boolean gcMetricsEnabled) {
        super.setGcMetricsEnabled(gcMetricsEnabled);
    }

    @Value("${discovery.application.flt.memoryMetricsEnabled:true}")
    @Override
    public void setMemoryMetricsEnabled(boolean memoryMetricsEnabled) {
        super.setMemoryMetricsEnabled(memoryMetricsEnabled);
    }

    @Value("${discovery.application.flt.diskMetricsEnabled:false}")
    @Override
    public void setDiskMetricsEnabled(boolean diskMetricsEnabled) {
        super.setDiskMetricsEnabled(diskMetricsEnabled);
    }

    @Value("${discovery.application.flt.diskIoMetricsEnabled:false}")
    @Override
    public void setDiskIoMetricsEnabled(boolean diskIoMetricsEnabled) {
        super.setDiskIoMetricsEnabled(diskIoMetricsEnabled);
    }

    @Value("${discovery.application.flt.netMetricsEnabled:false}")
    @Override
    public void setNetMetricsEnabled(boolean netMetricsEnabled) {
        super.setNetMetricsEnabled(netMetricsEnabled);
    }

    @Value("${discovery.application.flt.cpuMetricsEnabled:false}")
    @Override
    public void setCpuMetricsEnabled(boolean cpuMetricsEnabled) {
        super.setCpuMetricsEnabled(cpuMetricsEnabled);
    }

    @Value("${discovery.application.flt.envMetricsEnabled:true}")
    @Override
    public void setEnvMetricsEnabled(boolean envMetricsEnabled) {
        super.setEnvMetricsEnabled(envMetricsEnabled);
    }

    /**
     * 256k
     * @param maxQueueSize
     */
    @Value("${discovery.application.flt.maxQueueSize:262144}")
    @Override
    public void setMaxQueueSize(int maxQueueSize) {
        super.setMaxQueueSize(maxQueueSize);
    }

    @Value("${discovery.application.flt.maxReportThreads:1}")
    @Override
    public void setMaxReportThreads(int maxReportThreads) {
        super.setMaxReportThreads(maxReportThreads);
    }

    @Value("${discovery.application.flt.maxReportConnections:1}")
    @Override
    public void setMaxReportConnections(int maxReportConnections) {
        super.setMaxReportConnections(maxReportConnections);
    }

    @Value("${discovery.application.flt.maxTransportBatchSize:2048}")
    @Override
    public void setMaxTransportBatchSize(int maxTransportBatchSize) {
        super.setMaxTransportBatchSize(maxTransportBatchSize);
    }

    @Value("${discovery.application.flt.printQueueLength:false}")
    @Override
    public void setPrintQueueLength(boolean printQueueLength) {
        super.setPrintQueueLength(printQueueLength);
    }

    @Value("${discovery.application.flt.threshold:0}")
    @Override
    public void setThreshold(double threshold) {
        super.setThreshold(threshold);
    }

    @Value("${discovery.application.flt.rpcRequestTimeoutSeconds:30}")
    @Override
    public void setRpcRequestTimeoutSeconds(int rpcRequestTimeoutSeconds) {
        super.setRpcRequestTimeoutSeconds(rpcRequestTimeoutSeconds);
    }

    @Value("${discovery.application.flt.cpuSampleIntervalSeconds:15}")
    @Override
    public void setCpuSampleIntervalSeconds(int cpuSampleIntervalSeconds) {
        super.setCpuSampleIntervalSeconds(cpuSampleIntervalSeconds);
    }

    /**
     * 磁盘数据抽样间隔
     * @param diskSpaceSampleIntervalSeconds
     */
    @Value("${discovery.application.flt.diskSpaceSampleIntervalSeconds:300}")
    @Override
    public void setDiskSpaceSampleIntervalSeconds(int diskSpaceSampleIntervalSeconds) {
        super.setDiskSpaceSampleIntervalSeconds(diskSpaceSampleIntervalSeconds);
    }

    @Value("${discovery.application.flt.diskIoSampleIntervalSeconds:15}")
    @Override
    public void setDiskIoSampleIntervalSeconds(int diskIoSampleIntervalSeconds) {
        super.setDiskIoSampleIntervalSeconds(diskIoSampleIntervalSeconds);
    }

    @Value("${discovery.application.flt.memorySampleIntervalSeconds:15}")
    @Override
    public void setMemorySampleIntervalSeconds(int memorySampleIntervalSeconds) {
        super.setMemorySampleIntervalSeconds(memorySampleIntervalSeconds);
    }

    @Value("${discovery.application.flt.networkSampleIntervalSeconds:15}")
    @Override
    public void setNetworkSampleIntervalSeconds(int networkSampleIntervalSeconds) {
        super.setNetworkSampleIntervalSeconds(networkSampleIntervalSeconds);
    }

    @Value("${discovery.application.flt.gcSampleIntervalSeconds:5}")
    @Override
    public void setGcSampleIntervalSeconds(int gcSampleIntervalSeconds) {
        super.setGcSampleIntervalSeconds(gcSampleIntervalSeconds);
    }

    public boolean isFltDisabled() {
        return fltDisabled;
    }

    public void setFltDisabled(boolean fltDisabled) {
        this.fltDisabled = fltDisabled;
    }
}
