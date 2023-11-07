package rabbit.discovery.api.rest;

import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.rest.http.HttpRequest;

/**
 * open api 的负载均衡器
 */
public interface OpenLoadBalancer {

    /**
     * 选择目标服务节点
     * @param openRequest
     * @return
     */
    ServerNode choose(HttpRequest openRequest);
}
