package rabbit.discovery.api.test.spi;

import rabbit.discovery.api.common.rpc.ApiDescription;
import rabbit.discovery.api.rest.report.ReportListener;

import java.util.List;

public class MyReportListener implements ReportListener {

    @Override
    public void beforeReport(String className, List<ApiDescription> apiList) {
        ApiCache.getMap().put(className, apiList);
    }
}
