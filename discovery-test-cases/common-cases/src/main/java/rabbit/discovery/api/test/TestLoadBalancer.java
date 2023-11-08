package rabbit.discovery.api.test;

import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.rest.LoadBalancer;


/**
 * 测试专用负载均衡器
 */
public class TestLoadBalancer implements LoadBalancer {

    private int port;

    @Override
    public ServerNode choose(String application, String cluster) {
        return new ServerNode("localhost", port);
    }

    public void setPort(int port) {
        this.port = port;
    }
}
