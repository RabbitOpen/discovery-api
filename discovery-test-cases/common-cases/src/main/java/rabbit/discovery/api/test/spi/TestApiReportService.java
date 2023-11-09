package rabbit.discovery.api.test.spi;

import rabbit.discovery.api.common.rpc.ApiDescription;
import rabbit.discovery.api.common.rpc.ApiReportService;

import java.util.List;

public class TestApiReportService implements ApiReportService {

    @Override
    public void doReport(String application, String className, List<ApiDescription> apiList) {
        ApiCache.getMap().put(className, apiList);
    }

}
