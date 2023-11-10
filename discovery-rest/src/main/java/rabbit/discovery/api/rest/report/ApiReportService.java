package rabbit.discovery.api.rest.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.rpc.ApiData;
import rabbit.discovery.api.common.rpc.ApiDescription;
import rabbit.discovery.api.common.rpc.ProtocolServiceWrapper;
import rabbit.flt.common.utils.StringUtils;

import java.util.List;
import java.util.ServiceLoader;

/**
 * 接口上报服务代理对象
 */
class ApiReportService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static ApiReportService inst = new ApiReportService();

    private Configuration configuration;

    private ApiReportService() {
    }

    /**
     * 上报接口
     * @param className
     * @param apiList
     */
    public static void doReport(String className, List<ApiDescription> apiList) {
        if (StringUtils.isEmpty(inst.configuration.getRegistryAddress())) {
            inst.logger.error("report server 配置缺失，上报失败");
            return;
        }
        try {
            ServiceLoader.load(ReportListener.class).forEach(l -> l.beforeReport(className, apiList));
            ProtocolServiceWrapper.doReport(inst.configuration.getApplicationCode(),
                    new ApiData(className, apiList));
        } catch (Exception e) {
            inst.logger.error(e.getMessage());
        }
    }

    public static void setConfiguration(Configuration configuration) {
        if (null == inst.configuration) {
            inst.configuration = configuration;
            ProtocolServiceWrapper.init(configuration);
        }
    }
}
