package rabbit.discovery.api.rest.report;

import rabbit.discovery.api.common.rpc.ApiDescription;

import java.util.List;

public interface ReportListener {

    void beforeReport(String className, List<ApiDescription> apiList);
}
