package rabbit.discovery.api.common;

public final class Headers {

    private Headers() {}

    /**
     * api 版本
     */
    public static final String API_VERSION = "discovery-api-sdk-version";

    /**
     * 消费方应用编码
     */
    public static final String APPLICATION_CODE = "discovery-api-consumer-app-code";

    /**
     * 请求时间
     */
    public static final String REQUEST_TIME = "discovery-api-request-time";

    /**
     * 请求时间签名
     */
    public static final String REQUEST_TIME_SIGNATURE = "discovery-api-request-time-signature";

}
