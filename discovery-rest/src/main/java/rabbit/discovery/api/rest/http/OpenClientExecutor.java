package rabbit.discovery.api.rest.http;

import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.rest.HttpRequestExecutor;

/**
 * open api executor
 */
public final class OpenClientExecutor extends HttpRequestExecutor {

    @Override
    protected ServerNode getTargetServer(HttpRequest request) {
        return getOpenLoadBalancer().choose(request);
    }

    @Override
    protected String getServerAddress(ServerNode targetServer) {
        return targetServer.address().concat(targetServer.getPath());
    }
}
