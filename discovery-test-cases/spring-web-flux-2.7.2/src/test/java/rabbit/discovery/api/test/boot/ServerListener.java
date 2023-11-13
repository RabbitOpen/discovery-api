package rabbit.discovery.api.test.boot;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import rabbit.discovery.api.common.ConfigDetail;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.common.protocol.ApplicationInstance;
import rabbit.discovery.api.common.protocol.RegisterResult;
import rabbit.discovery.api.common.rpc.ApiData;
import rabbit.discovery.api.common.rpc.TcpProtocolService;
import rabbit.discovery.api.test.service.DiscoveryServiceImpl;
import rabbit.discovery.api.test.spi.ApiCache;
import rabbit.flt.rpc.common.ServerNode;
import rabbit.flt.rpc.common.rpc.ProtocolService;
import rabbit.flt.rpc.server.Server;
import rabbit.flt.rpc.server.ServerBuilder;
import reactor.core.publisher.Mono;

import java.net.StandardSocketOptions;
import java.util.Arrays;
import java.util.List;

public class ServerListener implements ApplicationListener, Ordered {

    Server server = null;

    @Override
    public synchronized void onApplicationEvent(ApplicationEvent event) {
        if (null != server) {
            return;
        }
        int port = 1899;
        server = ServerBuilder.builder()
                .workerThreadCount(2)
                .bossThreadCount(1)
                .host("localhost").port(port)
                .socketOption(StandardSocketOptions.SO_RCVBUF, 256 * 1024)
                .socketOption(StandardSocketOptions.SO_REUSEADDR, true)
                .registerHandler(ProtocolService.class, () -> Arrays.asList(new ServerNode("localhost", port)))
                .registerHandler(TcpProtocolService.class, new TcpProtocolService() {
                    @Override
                    public Mono<RegisterResult> register(ApplicationInstance instance) {
                        return Mono.just(DiscoveryServiceImpl.getInstance().register(instance));
                    }

                    @Override
                    public Mono<RegisterResult> keepAlive(ApplicationInstance instance) {
                        return Mono.just(DiscoveryServiceImpl.getInstance().keepAlive(instance));
                    }

                    @Override
                    public Mono<String> getPublicKey(String applicationCode) {
                        return Mono.just(DiscoveryServiceImpl.getInstance().getPublicKey(applicationCode));
                    }

                    @Override
                    public Mono<String> getRegistryAddress() {
                        return Mono.just(DiscoveryServiceImpl.getInstance().getRegistryAddress());
                    }

                    @Override
                    public Mono<PrivilegeData> getProviderPrivileges(String applicationCode) {
                        return Mono.just(DiscoveryServiceImpl.getInstance().getProviderPrivileges(applicationCode));
                    }

                    @Override
                    public Mono<ConfigDetail> loadConfig(String applicationCode, List<RemoteConfig> configFiles) {
                        return Mono.just(DiscoveryServiceImpl.getInstance().loadConfig(applicationCode, configFiles));
                    }

                    @Override
                    public Mono<Void> doReport(String application, ApiData apiData) {
                        ApiCache.getMap().put(apiData.getClassName(), apiData.getApiList());
                        return Mono.empty();
                    }
                })
                .maxFrameLength(16 * 1024 * 1024)
                .maxIdleSeconds(30)
                .maxPendingConnections(1000)
                .build();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::close));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
