package rabbit.discovery.api.rest.http;

import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.rest.HttpRequestExecutor;
import rabbit.discovery.api.rest.facotry.OpenClientFactory;

/**
 * open api executor
 */
public final class OpenClientExecutor extends HttpRequestExecutor {

    @Override
    protected ServerNode getTargetServer(HttpRequest request) {
        OpenClientFactory clientFactory = (OpenClientFactory) request.getClientFactory();
        return clientFactory.getServerNode();
    }

    @Override
    protected String getServerAddress(ServerNode targetServer) {
        return targetServer.address().concat(targetServer.getPath());
    }
}
