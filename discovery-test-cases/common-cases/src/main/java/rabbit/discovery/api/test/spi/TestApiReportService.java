package rabbit.discovery.api.test.spi;

import rabbit.discovery.api.common.rpc.ApiDescription;
import rabbit.discovery.api.common.rpc.ApiReportService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestApiReportService implements ApiReportService {

    private static Map<String, List<ApiDescription>> map = new HashMap<>();

    @Override
    public void doReport(String application, String className, List<ApiDescription> apiList) {
        map.put(className, apiList);
    }

    public static Map<String, List<ApiDescription>> getMap() {
        return map;
    }
}
