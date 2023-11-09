package rabbit.discovery.api.test.spi;

import rabbit.discovery.api.common.rpc.ApiDescription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiCacheMap {

    private static Map<String, List<ApiDescription>> map = new HashMap<>();

    private ApiCacheMap() {}

    public static Map<String, List<ApiDescription>> getMap() {
        return map;
    }
}
