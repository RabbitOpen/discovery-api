package rabbit.discovery.api.rest.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.RpcFactory;
import rabbit.discovery.api.common.rpc.ApiDescription;
import rabbit.discovery.api.common.rpc.ApiReportService;
import rabbit.flt.common.utils.StringUtils;

import java.util.List;

/**
 * 接口上报服务代理对象
 */
class ReportServiceProxy implements ApiReportService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String securityKey;

    private String reportServer;

    @Override
    public void doReport(String application, String className, List<ApiDescription> apiList) {
        this.securityKey = securityKey.length() > 16 ? securityKey.substring(0, 16) : securityKey;
        if (StringUtils.isEmpty(reportServer)) {
            logger.error("report server 配置缺失，上报失败");
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
        Configuration configuration = new Configuration();
        configuration.setRegistryAddress(reportServer);
        configuration.setApplicationCode(application);
        configuration.setPrivateKey(securityKey);
        RpcFactory.init(configuration);
        return RpcFactory.proxy(ApiReportService.class);
    }

    @Override
    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    @Override
    public void setReportServer(String reportServer) {
        this.reportServer = reportServer;
    }

}
