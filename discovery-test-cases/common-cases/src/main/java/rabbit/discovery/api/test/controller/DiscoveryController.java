package rabbit.discovery.api.test.controller;

import org.springframework.web.bind.annotation.*;
import rabbit.discovery.api.common.PublicKeyDesc;
import rabbit.discovery.api.common.protocol.ApplicationInstance;
import rabbit.discovery.api.common.protocol.PrivilegeData;
import rabbit.discovery.api.common.protocol.RegisterResult;
import rabbit.discovery.api.test.service.DiscoveryServiceImpl;

/**
 * 注册服务
 */
@RestController
@RequestMapping("/discovery")
public class DiscoveryController {

    private DiscoveryServiceImpl discoveryService = DiscoveryServiceImpl.getInstance();

    /**
     * 注册自己
     *
     * @param instance
     * @return
     */
    @PostMapping("/register")
    public RegisterResult register(@RequestBody ApplicationInstance instance) {
        return discoveryService.register(instance);
    }

    /**
     * 维持心跳
     *
     * @param instance
     * @return
     */
    @PostMapping("/keepAlive")
    public RegisterResult keepAlive(@RequestBody ApplicationInstance instance) {
        return discoveryService.keepAlive(instance);
    }

    /**
     * 获取应用公钥
     *
     * @param applicationCode
     * @return
     */
    @GetMapping("/getPublicKey/{applicationCode:.+}")
    public PublicKeyDesc getPublicKey(@PathVariable("applicationCode") String applicationCode) {
        return discoveryService.getPublicKey(applicationCode);
    }

    /**
     * 获取注册中心地址
     */
    @GetMapping("/getRegistryAddress")
    public String getRegistryAddress() {
        return discoveryService.getRegistryAddress();
    }

    /**
     * 获取自己授权出去的权限
     *
     * @param applicationCode
     * @return
     */
    @PostMapping("/authorizations/provider/{applicationCode:.+}")
    public PrivilegeData getProviderPrivileges(@PathVariable("applicationCode") String applicationCode) {
        return discoveryService.getProviderPrivileges(applicationCode);
    }

    public void incrementConfigVersion() {
        discoveryService.incrementConfigVersion();
    }
}
