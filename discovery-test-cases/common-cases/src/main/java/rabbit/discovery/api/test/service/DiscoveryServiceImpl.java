package rabbit.discovery.api.test.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.Privilege;
import rabbit.discovery.api.common.PublicKeyDesc;
import rabbit.discovery.api.common.protocol.*;
import rabbit.discovery.api.common.rpc.ProtocolService;
import rabbit.discovery.api.common.utils.JsonUtils;
import rabbit.flt.common.utils.GZipUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static rabbit.discovery.api.common.utils.PathParser.urlDecode;

public class DiscoveryServiceImpl implements ProtocolService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final DiscoveryServiceImpl inst = new DiscoveryServiceImpl();

    private int port = 1802;

    private ApplicationMeta applicationMeta;

    private DiscoveryServiceImpl() {
        applicationMeta = new ApplicationMeta();
        applicationMeta.setAuthVersion(1L);
        applicationMeta.setRegistryAddressVersion(1L);
        applicationMeta.setPrivilegeVersion(1L);
    }

    @Override
    public RegisterResult register(ApplicationInstance instance, Map<String, String> signature) {
        logger.info("application[{}] instance[{}:{}] register success!", instance.getApplicationCode(),
                instance.getHost(), instance.getPort());
        Provider provider = applicationMeta.getProvider();
        provider.getInstanceGroupMetas().computeIfAbsent("restApiSampleServer", k -> {
            InstanceGroupMeta meta = new InstanceGroupMeta();
            meta.addGroupLoadBalance("default", "http://localhost:1802");
            meta.addGroupLoadBalance("local", "http://127.0.0.1:1802");
            return meta;
        });
        return RegisterResult.success("1", applicationMeta);
    }

    @Override
    public RegisterResult keepAlive(ApplicationInstance instance, Map<String, String> signature) {
        logger.info("application[{}] instance[{}:{}] keepAlive success!", instance.getApplicationCode(),
                instance.getHost(), instance.getPort());
        return RegisterResult.success("1", applicationMeta);
    }

    @Override
    public PublicKeyDesc getPublicKey(String applicationCode, Map<String, String> signature) {
        logger.info("load app[{}] public key success!", urlDecode(applicationCode));
        String publicKey = "305C300D06092A864886F70D0101010500034B003048024100C5B76A3974FEED9144066469D95D3A0297288F626A54A3624901552353DFBDA20FA4156CE11C6048FC3F9DB79101DB047933E031074719C10D552E05658D16290203010001";
        PublicKeyDesc publicKeyDesc = new PublicKeyDesc();
        publicKeyDesc.setPublicKey(publicKey);
        publicKeyDesc.setKeyVersion(1L);
        return publicKeyDesc;
    }

    @Override
    public String getRegistryAddress(Map<String, String> signature) {
        logger.info("load registry address success!");
        return "localhost:".concat(Integer.toString(port));
    }

    @Override
    public PrivilegeData getProviderPrivileges(String applicationCode, Map<String, String> signature) {
        String appCode = urlDecode(applicationCode);
        logger.info("application[{}] load privilege data success!", appCode);
        PrivilegeData privilegeData = new PrivilegeData();
        List<Privilege> list = new ArrayList<>();
        Privilege data = new Privilege();
        data.setProvider(appCode);
        data.setConsumer(appCode);
        data.setPath("/black/authorized");
        list.add(data);
        byte[] bytes = JsonUtils.writeObject(list).getBytes();
        privilegeData.setCompressedPrivileges(GZipUtils.compress(bytes));
        privilegeData.setPlainDataLength(bytes.length);
        return privilegeData;
    }

    public void incrementConfigVersion() {
        applicationMeta.setConfigVersion(this.applicationMeta.getConfigVersion() + 1);
    }

    public static DiscoveryServiceImpl getInstance() {
        return inst;
    }
}
