package rabbit.discovery.api.rest;

import rabbit.discovery.api.common.ServerNode;

/**
 * rest 接口 负载均衡
 */
public interface LoadBalancer {

    /**
     * 选择服务节点
     *
     * @param application 目标应用
     * @param group       目标应用分组信息
     * @return
     */
    ServerNode choose(String application, String group);
}
