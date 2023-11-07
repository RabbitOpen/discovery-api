package rabbit.discovery.api.common;

import rabbit.flt.rpc.client.FltRequestFactory;
import rabbit.flt.rpc.client.pool.ConfigBuilder;
import rabbit.flt.rpc.common.ServerNode;

import java.util.ArrayList;
import java.util.List;

public class RpcFactory {

    private static final RpcFactory inst = new RpcFactory();

    private FltRequestFactory requestFactory = new FltRequestFactory();

    private Configuration configuration;

    private RpcFactory() {}

    /**
     * 初始化
     * @param configuration
     */
    public static synchronized void init(Configuration configuration) {
        if (null == inst.configuration) {
            inst.configuration = configuration;
            inst.doInitialization();
        }
    }

    private void doInitialization() {
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
                .acquireClientTimeoutSeconds(10)
                .applicationCode(configuration.getApplicationCode())
                .workerThreadCount(8)
                .bossThreadCount(1)
                .password(configuration.getPrivateKey().substring(0, 16))
                .build());
    }
}
