package rabbit.discovery.api.test.controller;

import org.springframework.web.bind.annotation.*;
import rabbit.discovery.api.common.ConfigDetail;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.common.protocol.ApplicationInstance;
import rabbit.discovery.api.common.protocol.PrivilegeData;
import rabbit.discovery.api.common.protocol.RegisterResult;
import rabbit.discovery.api.common.rpc.ApiData;
import rabbit.discovery.api.test.service.DiscoveryServiceImpl;

import java.util.List;

/**
 * 注册服务
 */
@RestController
@RequestMapping("/discovery")
public class DiscoveryController {

    private DiscoveryServiceImpl discoveryService = DiscoveryServiceImpl.getInstance();

    /**
     * 读取配置
     *
     * @param applicationCode
     * @param configFiles
     * @return
     */
    // !!! 注入applicationCode格式，防止（spring4下）被截断
    @PostMapping("/load/{applicationCode:.+}")
    public ConfigDetail loadConfig(@PathVariable("applicationCode") String applicationCode,
                                   @RequestBody(required = false) List<RemoteConfig> configFiles) {
        return discoveryService.loadConfig(applicationCode, configFiles);
    }

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
    public String getPublicKey(@PathVariable("applicationCode") String applicationCode) {
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

    /**
     * 接口上报
     * @param applicationCode
     * @param apiData
     */
    @PostMapping("/discovery/api/report/{applicationCode:.+}")
    public void doReport(@PathVariable("applicationCode") String applicationCode,
                         @RequestBody ApiData apiData) {
        discoveryService.doReport(applicationCode, apiData);
    }

    public void incrementConfigVersion() {
        discoveryService.incrementConfigVersion();
    }

    /**
     * 设置并刷新配置版本
     * @param age
     * @param companyName
     */
    public void update(int age, String companyName) {
        discoveryService.update(age, companyName);
    }

    public int getAge() {
        return discoveryService.getAge();
    }

    public String getName() {
        return discoveryService.getName();
    }

    public String getGender() {
        return discoveryService.getGender();
    }

    public String getCompanyName() {
        return discoveryService.getCompanyName();
    }

    public String getCompanyAddress() {
        return discoveryService.getCompanyAddress();
    }
}
