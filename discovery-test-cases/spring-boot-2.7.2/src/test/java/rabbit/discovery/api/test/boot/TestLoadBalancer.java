package rabbit.discovery.api.test.boot;

import org.springframework.stereotype.Component;
import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.rest.LoadBalancer;

@Component
public class TestLoadBalancer implements LoadBalancer {

    private int port;

    @Override
    public ServerNode choose(String application, String group) {
        return new ServerNode("localhost", port);
    }

    public void setPort(int port) {
        this.port = port;
    }
}
