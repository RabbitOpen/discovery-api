package rabbit.discovery.api.rest.http;

import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.rest.HttpRequestExecutor;

/**
 * rest api executor
 */
public final class RestClientExecutor extends HttpRequestExecutor {

    /**
     * 通过负载均衡器选择服务器节点
     * @param request
     * @return
     */
    @Override
    protected ServerNode getTargetServer(HttpRequest request) {
        return getLoadBalancer().choose(request);
    }

    @Override
    protected String getServerAddress(ServerNode targetServer) {
        return targetServer.address();
    }
}
