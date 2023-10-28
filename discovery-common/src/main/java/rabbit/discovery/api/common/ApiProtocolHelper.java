package rabbit.discovery.api.common;

import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.utils.HexUtils;
import rabbit.discovery.api.common.utils.RsaUtils;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ApiProtocolHelper {

    private static final ApiProtocolHelper helper = new ApiProtocolHelper();

    private PrivateKey privateKey;

    private ApiProtocolHelper() {
    }

    /**
     * 添加协议头
     *
     * @param consumer
     * @param configuration
     */
    public static void addProtocolHeader(BiConsumer<String, String> consumer, Configuration configuration) {
        try {
            String requestTime = Long.toString(System.currentTimeMillis());
            consumer.accept(Headers.API_VERSION, Environment.getVersion());
            consumer.accept(Headers.APPLICATION_CODE, configuration.getApplicationCode());
            consumer.accept(Headers.REQUEST_TIME, requestTime);
            consumer.accept(Headers.REQUEST_TIME_SIGNATURE, getSignature(requestTime, configuration));
        } catch (Exception e) {
            throw new DiscoveryException(e);
        }
    }

    private static String getSignature(String requestTime, Configuration configuration) {
        if (null == helper.privateKey) {
            helper.privateKey = RsaUtils.loadPrivateKeyFromString(configuration.getPrivateKey());
        }
        return HexUtils.toHex(RsaUtils.signWithPrivateKey(requestTime, helper.privateKey));
    }

    /**
     * 生成请求签名头
     * @param application
     * @param privateKey
     * @return
     */
    public static Map<String, String> getSignatureMap(String application, PrivateKey privateKey) {
        Map<String, String> map = new HashMap<>();
        String now = Long.toString(System.currentTimeMillis());
        map.put(Headers.REQUEST_TIME, now);
        map.put(Headers.APPLICATION_CODE, application);
        map.put(Headers.REQUEST_TIME_SIGNATURE, HexUtils.toHex(RsaUtils.signWithPrivateKey(now, privateKey)));
        return map;
    }
}
