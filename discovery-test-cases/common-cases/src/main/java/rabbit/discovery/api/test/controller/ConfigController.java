package rabbit.discovery.api.test.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rabbit.discovery.api.common.ConfigDetail;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.test.service.ConfigServiceImpl;

import java.util.HashMap;
import java.util.List;

/**
 * 配置controller
 */
@RestController
public class ConfigController {

    private ConfigServiceImpl configService = ConfigServiceImpl.getInstance();

    /**
     * 读取配置
     *
     * @param applicationCode
     * @param configFiles
     * @return
     */
    // !!! 注入applicationCode格式，防止（spring4下）被截断
    @PostMapping("/config/load/{applicationCode:.+}")
    public ConfigDetail loadConfig(@PathVariable("applicationCode") String applicationCode,
                                   @RequestBody(required = false) List<RemoteConfig> configFiles) {
        return configService.loadConfig(applicationCode, configFiles, new HashMap<>());
    }

    /**
     * 设置并刷新配置版本
     * @param age
     * @param companyName
     */
    public void update(int age, String companyName) {
        configService.update(age, companyName);
    }

    public int getAge() {
        return configService.getAge();
    }

    public String getName() {
        return configService.getName();
    }

    public String getGender() {
        return configService.getGender();
    }

    public String getCompanyName() {
        return configService.getCompanyName();
    }

    public String getCompanyAddress() {
        return configService.getCompanyAddress();
    }
}
