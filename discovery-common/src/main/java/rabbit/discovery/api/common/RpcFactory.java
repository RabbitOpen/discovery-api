package rabbit.discovery.api.common;

import rabbit.discovery.api.common.rpc.HttpProtocolService;
import rabbit.discovery.api.common.rpc.TcpProtocolService;
import rabbit.flt.rpc.client.FltRequestFactory;
import rabbit.flt.rpc.client.pool.ConfigBuilder;
import rabbit.flt.rpc.common.ServerNode;

import java.util.ArrayList;
import java.util.List;

public class RpcFactory {

    private static final RpcFactory inst = new RpcFactory();

    private FltRequestFactory requestFactory = new FltRequestFactory();

    private Configuration configuration;

    private RpcFactory() {
    }

    private synchronized void doInitialization(Configuration configuration) {
        if (null != this.configuration) {
            return;
        }
        this.configuration = configuration;
        String servers = configuration.getRegistryAddress();
        List<ServerNode> nodes = new ArrayList<>();
        for (String s : servers.split(",")) {
            String[] split = s.trim().split(":");
            nodes.add(new ServerNode(split[0].trim(), Integer.parseInt(split[1].trim())));
        }
        requestFactory.init(ConfigBuilder.builder()
                .rpcRequestTimeoutSeconds(10)
                .serverNodes(nodes)
                .connectionsPerServer(1)
                .maxRetryTime(0)
                .acquireClientTimeoutSeconds(10)
                .applicationCode(configuration.getApplicationCode())
                .requestInterceptor(request -> {
                    if (HttpProtocolService.class == request.getRequest().getInterfaceClz()) {
                        request.getRequest().setHandlerInterfaceName(TcpProtocolService.class.getName());
                    }
                })
                .workerThreadCount(8)
                .bossThreadCount(1)
                // 降低内置心跳频率，业务上不依赖该操作维持心跳
                .keepAliveIntervalSeconds(1800)
                .password(configuration.getPrivateKey().substring(0, 16))
                .build());
    }

    /**
     * 代理接口
     *
     * @param clz
     * @param configuration
     * @param <T>
     * @return
     */
    public static <T> T proxy(Class<T> clz, Configuration configuration) {
        if (null == inst.configuration) {
            inst.doInitialization(configuration);
        }
        return inst.requestFactory.proxy(clz);
    }
}
