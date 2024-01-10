package rabbit.discovery.api.common.global;

import rabbit.discovery.api.common.global.bean.AuthorizedURI;
import rabbit.discovery.api.common.utils.PathPattern;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 授权明细缓存
 */
public class AuthorizationDetail {

    private static final AuthorizationDetail detail = new AuthorizationDetail();

    /**
     * 消费方授权详情
     */
    private Map<String, List<AuthorizedURI>> authorizationDetails = new ConcurrentHashMap<>();

    private AuthorizationDetail() {}

    public static Map<String, List<AuthorizedURI>> getAuthorizationDetails() {
        return detail.authorizationDetails;
    }

    public static void setAuthorizationDetails(Map<String, List<AuthorizedURI>> authorizationDetails) {
        detail.authorizationDetails = authorizationDetails;
    }
}
