package rabbit.discovery.api.test.boot;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rabbit.discovery.api.common.rpc.ApiReportService;
import rabbit.discovery.api.common.rpc.ConfigService;
import rabbit.discovery.api.test.service.ConfigServiceImpl;
import rabbit.discovery.api.test.service.DiscoveryServiceImpl;
import rabbit.discovery.api.test.spi.ApiCacheMap;
import rabbit.flt.common.Metrics;
import rabbit.flt.rpc.common.ServerNode;
import rabbit.flt.rpc.common.rpc.ProtocolService;
import rabbit.flt.rpc.server.Server;
import rabbit.flt.rpc.server.ServerBuilder;

import java.net.StandardSocketOptions;
import java.util.Arrays;
import java.util.List;

public class MySpringRunner extends SpringJUnit4ClassRunner {

    public MySpringRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        int port = 1899;
        Server server = ServerBuilder.builder()
                .workerThreadCount(2)
                .bossThreadCount(1)
                .host("localhost").port(port)
                .socketOption(StandardSocketOptions.SO_RCVBUF, 256 * 1024)
                .socketOption(StandardSocketOptions.SO_REUSEADDR, true)
                .registerHandler(ProtocolService.class, new ProtocolService() {
                    @Override
                    public List<ServerNode> getServerNodes() {
                        return Arrays.asList(new ServerNode("localhost", port));
                    }

                    @Override
                    public boolean isMetricsEnabled(String applicationCode, Class<? extends Metrics> type) {
                        return false;
                    }
                })
                .registerHandler(rabbit.discovery.api.common.rpc.ProtocolService.class, DiscoveryServiceImpl.getInstance())
                .registerHandler(ConfigService.class, ConfigServiceImpl.getInstance())
                .registerHandler(ApiReportService.class, (application, className, apiList) -> ApiCacheMap.getMap().put(className, apiList))
                .maxFrameLength(16 * 1024 * 1024)
                .maxIdleSeconds(30)
                .maxPendingConnections(1000)
                .build();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::close));
    }

}
