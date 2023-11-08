package rabbit.discovery.api.rest.http;

import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.rest.HttpRequestExecutor;
import rabbit.flt.common.utils.StringUtils;

/**
 * open api executor
 */
public final class OpenClientExecutor extends HttpRequestExecutor {

    @Override
    protected ServerNode getTargetServer(HttpRequest request) {
        ServerNode targetServer = request.getTargetServer();
        return null == targetServer ? getOpenLoadBalancer().choose(request) : targetServer;
    }

    @Override
    protected String getServerAddress(ServerNode targetServer) {
        return targetServer.address().concat(StringUtils.isEmpty(targetServer.getPath()) ? "" : targetServer.getPath());
    }
}
