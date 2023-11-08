package rabbit.discovery.api.rest;

import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.rest.http.HttpRequest;

/**
 * rest 接口 负载均衡
 */
public interface LoadBalancer {

    /**
     * 选择服务节点
     *
     * @param request 请求
     * @return
     */
    ServerNode choose(HttpRequest request);
}
