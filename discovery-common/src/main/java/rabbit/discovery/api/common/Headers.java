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


    // ------------------------  开放接口header  ------------------------

    /**
     * 开放接口编码
     */
    public static final String OPEN_API_CODE = "open-api-code";

    /**
     * 开放接口凭据
     */
    public static final String OPEN_API_CREDENTIAL = "open-api-credential";

    /**
     * 开放接口请求时间
     */
    public static final String OPEN_API_REQUEST_TIME = "open-api-request-time";

    /**
     * 开放接口请求时间签名
     */
    public static final String OPEN_API_REQUEST_TIME_SIGNATURE = "open-api-request-time-signature";

}
