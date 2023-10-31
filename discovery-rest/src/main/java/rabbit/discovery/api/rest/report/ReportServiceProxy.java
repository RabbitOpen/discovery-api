package rabbit.discovery.api.rest.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.rpc.ApiDescription;
import rabbit.discovery.api.common.rpc.ApiReportService;
import rabbit.flt.common.utils.StringUtils;
import rabbit.flt.rpc.client.AgentRequestFactory;
import rabbit.flt.rpc.client.pool.ConfigBuilder;
import rabbit.flt.rpc.common.ServerNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 接口上报服务代理对象
 */
class ReportServiceProxy implements ApiReportService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String securityKey;

    private String agentServers;

    private AgentRequestFactory requestFactory = new AgentRequestFactory();

    @Override
    public void doReport(String application, String className, List<ApiDescription> apiList) {
        this.securityKey = securityKey.length() > 16 ? securityKey.substring(0, 16) : securityKey;
        if (StringUtils.isEmpty(agentServers)) {
            logger.error("agent server 配置缺失，上报失败");
            return;
        }
        ApiReportService realService = getRealReportService(application);
        if (null == realService) {
            return;
        }
        try {
            realService.doReport(application, className, apiList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private ApiReportService getRealReportService(String application) {
        List<ServerNode> nodes = new ArrayList<>();
        for (String server : agentServers.split(",")) {
            String[] split = server.trim().split(":");
            nodes.add(new ServerNode(split[0].trim(), Integer.parseInt(split[1].trim())));
        }
        requestFactory.init(ConfigBuilder.builder()
                .serverNodes(nodes)
                .applicationCode(application)
                .password(securityKey)
                .build());
        return requestFactory.proxy(ApiReportService.class);
    }

    @Override
    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    @Override
    public void setAgentServer(String agentServer) {
        this.agentServers = agentServer;
    }
}
