package rabbit.discovery.api.common.ext;

public interface Interceptor {

    /**
     * 请求拦截
     * @param request
     * @return  true, 业务系统自己接管
     */
    boolean intercept(HttpRequest request);
}
