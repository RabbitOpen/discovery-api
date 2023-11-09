package rabbit.discovery.api.common.rpc;

import java.util.List;

/**
 * 接口上报服务
 */
public interface ApiReportService {


    /**
     * 上报接口
     * @param application  应用
     * @param className    接口所属的类
     * @param apiList      接口清单
     */
    void doReport(String application, String className, List<ApiDescription> apiList);

    /**
     * 设置密钥
     * @param securityKey
     */
    default void setSecurityKey(String securityKey) {
        // 设置密钥
    }

    /**
     * 设置服务地址
     * @param reportServer
     */
    default void setReportServer(String reportServer) {
        // 设置服务地址
    }

}
