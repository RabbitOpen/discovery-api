package rabbit.discovery.api.test.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rabbit.discovery.api.common.ConfigDetail;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.common.enums.ConfigType;
import rabbit.discovery.api.common.utils.PathParser;
import rabbit.flt.common.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置controller
 */
@RestController
public class ConfigController {

    private long configVersion = 0L;

    private int age = 10;

    private String name = "zhang3";

    private String gender = "male";

    private String companyName = "alibaba";

    private String companyAddress = "chengdu";

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
        if (CollectionUtils.isEmpty(configFiles)) {
            return new ConfigDetail(new ArrayList<>(), 1L);
        }
        List<RemoteConfig> configs = new ArrayList<>();
        RemoteConfig rc = new RemoteConfig();
        rc.setNamespace(configFiles.get(0).getNamespace());
        rc.setPriority(-2);
        rc.setType(ConfigType.YAML);
        rc.setName(configFiles.get(0).getName());
        rc.setApplicationCode(PathParser.urlDecode(applicationCode));
        rc.setContent("people: \n" +
                "  age: " + age + "\n" +
                "  gender: " + getGender() + "\n" +
                "  company:\n" +
                "    address: " + getCompanyAddress() + "\n" +
                "    name: " + getCompanyName() + "\n" +
                "  name: " + getName() + "\n"
        );
        configs.add(rc);
        return new ConfigDetail(configs, configVersion);
    }

    /**
     * 设置并刷新配置版本
     * @param age
     * @param companyName
     */
    public void update(int age, String companyName) {
        this.age = age;
        this.companyName = companyName;
        this.configVersion++;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public long getConfigVersion() {
        return configVersion;
    }

    public String getGender() {
        return gender;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }
}
