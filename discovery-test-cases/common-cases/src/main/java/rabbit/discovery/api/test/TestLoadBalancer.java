package rabbit.discovery.api.test;

import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.rest.LoadBalancer;
import rabbit.discovery.api.rest.http.HttpRequest;


/**
 * 测试专用负载均衡器
 */
public class TestLoadBalancer implements LoadBalancer {

    private int port;

    @Override
    public ServerNode choose(HttpRequest request) {
        return new ServerNode("localhost", port);
    }

    public void setPort(int port) {
        this.port = port;
    }
}
