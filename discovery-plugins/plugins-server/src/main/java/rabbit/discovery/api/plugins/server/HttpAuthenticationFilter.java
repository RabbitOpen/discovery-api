package rabbit.discovery.api.plugins.server;

import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.Headers;
import rabbit.discovery.api.common.PublicKeyManager;
import rabbit.discovery.api.common.SpringBeanSupplierHolder;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.ext.HttpRequest;
import rabbit.discovery.api.common.ext.Interceptor;
import rabbit.discovery.api.common.global.ApplicationMetaCache;
import rabbit.discovery.api.common.global.AuthorizationDetail;
import rabbit.discovery.api.common.global.bean.AuthorizedURI;
import rabbit.discovery.api.common.utils.HexUtils;
import rabbit.discovery.api.common.utils.PathParser;
import rabbit.discovery.api.common.utils.PathPattern;
import rabbit.discovery.api.common.utils.RsaUtils;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static rabbit.discovery.api.common.enums.SecurityMode.WHITE;
import static rabbit.flt.common.utils.StringUtils.isEmpty;

public abstract class HttpAuthenticationFilter extends SpringBeanSupplierHolder {

    /**
     * 认证
     *
     * @param httpRequest
     */
    protected void authenticate(HttpRequest httpRequest) {
        if (null != getInterceptor() && getInterceptor().intercept(httpRequest)) {
            // 业务系统自定义拦截以后插件将不再受理请求
            return;
        }
        doAuthentication(httpRequest);
    }

    /**
     * 鉴权
     *
     * @param httpRequest
     */
    private void doAuthentication(HttpRequest httpRequest) {
        if (isTrustableRequest(httpRequest)) {
            // 白名单 & 非黑名单 直接放行
            return;
        }
        // 鉴别请求方身份
        identifyRequest(httpRequest);
        if (isWhiteConsumer(httpRequest.getConsumer())) {
            // 白名单应用直接放行
            return;
        }
        if (isAuthorizedRequest(httpRequest)) {
            return;
        }
        throw new DiscoveryException("unAuthorized api[".concat(httpRequest.getConsumer()).concat("].[")
                .concat(httpRequest.getUrl()).concat("]"));
    }

    /**
     * 判断是不是授权请求
     *
     * @param request
     * @return
     */
    private boolean isAuthorizedRequest(HttpRequest request) {
        List<AuthorizedURI> uriList = AuthorizationDetail.getAuthorizationDetails().getOrDefault(request.getConsumer(), new ArrayList<>());
        for (AuthorizedURI uri : uriList) {
            if (uri.getPattern().match(request.getUrl()) && request.getMethod().equals(uri.getMethod())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 白名单消费者
     *
     * @param consumer
     * @return
     */
    private boolean isWhiteConsumer(String consumer) {
        return ApplicationMetaCache.getApplicationMeta().getWhiteConsumers().contains(consumer);
    }

    /**
     * 受信请求
     *
     * @param request
     * @return
     */
    private boolean isTrustableRequest(HttpRequest request) {
        String url = request.getUrl();
        String patterns = getConfiguration().getPatterns();
        if (WHITE == getConfiguration().getMode()) {
            if (!isEmpty(patterns)) {
                return match(url, patterns);
            }
            return false;
        } else {
            if (!isEmpty(patterns)) {
                return !match(url, patterns);
            }
            return true;
        }
    }

    private void identifyRequest(HttpRequest request) {
        String consumer = request.getConsumer();
        String errorMsg = "request[".concat(request.getUrl()).concat("] error: ");
        if (isEmpty(consumer)) {
            throw new DiscoveryException(errorMsg.concat("消费方信息不能为空"));
        }
        String requestTimeStr = request.getHeader(Headers.REQUEST_TIME);
        if (isEmpty(requestTimeStr)) {
            throw new DiscoveryException(errorMsg.concat("请求时间不能为空"));
        }
        String signature = request.getHeader(Headers.REQUEST_TIME_SIGNATURE);
        if (isEmpty(signature)) {
            throw new DiscoveryException(errorMsg.concat("请求签名不能为空"));
        }
        long requestTime;
        try {
            if (!verifySignature(consumer, requestTimeStr, signature)) {
                throw new DiscoveryException(errorMsg.concat("验签失败"));
            }
            requestTime = Long.parseLong(requestTimeStr);
        } catch (Exception e) {
            throw new DiscoveryException(errorMsg.concat(e.getMessage()));
        }
        long replayWindow = getConfiguration().getReplayWindow() * 1000L;
        long actual = Math.abs(System.currentTimeMillis() - requestTime);
        if (actual > replayWindow) {
            throw new DiscoveryException(errorMsg.concat("请求时间偏差过大！预计：")
                    .concat(Long.toString(replayWindow)).concat("ms, 实际：")
                    .concat(Long.toString(replayWindow)).concat("ms"));
        }
    }

    private boolean verifySignature(String consumer, String requestTimeStr, String signature) {
        PublicKeyManager.setConfiguration(getConfiguration());
        PublicKey publicKey = PublicKeyManager.getPublicKey(consumer);
        if (!RsaUtils.verifyWithPublicKey(HexUtils.toBytes(signature), requestTimeStr, publicKey)) {
            publicKey = PublicKeyManager.refreshPublicKey(consumer);
            return RsaUtils.verifyWithPublicKey(HexUtils.toBytes(signature), requestTimeStr, publicKey);
        } else {
            return true;
        }
    }

    private boolean match(String url, String patterns) {
        for (String pattern : patterns.trim().split(",")) {
            if (PathParser.parsePattern(pattern.trim()).match(url)) {
                return true;
            }
        }
        return false;
    }

    protected Configuration getConfiguration() {
        return getSpringBean(Configuration.class);
    }

    protected Interceptor getInterceptor() {
        return getSpringBean(Interceptor.class);
    }

    private <T> T getSpringBean(Class<T> clz) {
        if (null != supplier) {
            return supplier.getSpringBean(clz);
        }
        throw new DiscoveryException("no supplier for clz: " + clz);
    }
}
