package rabbit.discovery.api.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import rabbit.discovery.api.common.Privilege;
import rabbit.discovery.api.common.PublicKeyDesc;
import rabbit.discovery.api.common.protocol.*;
import rabbit.discovery.api.common.utils.JsonUtils;
import rabbit.flt.common.utils.GZipUtils;

import java.util.ArrayList;
import java.util.List;

import static rabbit.discovery.api.common.utils.PathParser.urlDecode;

/**
 * 注册服务
 */
@RestController
@RequestMapping("/discovery")
public class DiscoveryController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${server.port:1802}")
    private int port = 1802;

    private ApplicationMeta applicationMeta;

    public DiscoveryController() {
        applicationMeta = new ApplicationMeta();
        applicationMeta.setAuthVersion(1L);
        applicationMeta.setRegistryAddressVersion(1L);
        applicationMeta.setPrivilegeVersion(1L);
    }

    /**
     * 注册自己
     *
     * @param instance
     * @return
     */
    @PostMapping("/register")
    public RegisterResult register(@RequestBody ApplicationInstance instance) {
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

    /**
     * 维持心跳
     *
     * @param instance
     * @return
     */
    @PostMapping("/keepAlive")
    public RegisterResult keepAlive(@RequestBody ApplicationInstance instance) {
        logger.info("application[{}] instance[{}:{}] keepAlive success!", instance.getApplicationCode(),
                instance.getHost(), instance.getPort());
        return RegisterResult.success("1", applicationMeta);
    }

    /**
     * 获取应用公钥
     *
     * @param applicationCode
     * @return
     */
    @GetMapping("/getPublicKey/{applicationCode:.+}")
    public PublicKeyDesc getPublicKey(@PathVariable("applicationCode") String applicationCode) {
        logger.info("load app[{}] public key success!", urlDecode(applicationCode));
        String publicKey = "305C300D06092A864886F70D0101010500034B003048024100C5B76A3974FEED9144066469D95D3A0297288F626A54A3624901552353DFBDA20FA4156CE11C6048FC3F9DB79101DB047933E031074719C10D552E05658D16290203010001";
        PublicKeyDesc publicKeyDesc = new PublicKeyDesc();
        publicKeyDesc.setPublicKey(publicKey);
        publicKeyDesc.setKeyVersion(1L);
        return publicKeyDesc;
    }

    /**
     * 获取注册中心地址
     */
    @GetMapping("/getRegistryAddress")
    public String getRegistryAddress() {
        logger.info("load registry address success!");
        return "localhost:".concat(Integer.toString(port));
    }

    /**
     * 获取自己授权出去的权限
     *
     * @param applicationCode
     * @return
     */
    @PostMapping("/authorizations/provider/{applicationCode:.+}")
    public PrivilegeData getProviderPrivileges(@PathVariable("applicationCode") String applicationCode) {
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
}
