package rabbit.discovery.api.test.service;

import rabbit.discovery.api.common.ConfigDetail;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.common.enums.ConfigType;
import rabbit.discovery.api.common.rpc.ConfigService;
import rabbit.discovery.api.common.utils.PathParser;
import rabbit.flt.common.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ConfigServiceImpl implements ConfigService {

    private static final ConfigServiceImpl inst = new ConfigServiceImpl();

    private ConfigServiceImpl() {}

    private long configVersion = 0L;

    private int age = 10;

    private String name = "zhang3";

    private String gender = "male";

    private String companyName = "alibaba";

    private String companyAddress = "chengdu";

    @Override
    public ConfigDetail loadConfig(String applicationCode, List<RemoteConfig> configFiles) {
        if (CollectionUtils.isEmpty(configFiles)) {
            return new ConfigDetail(new ArrayList<>(), 1L);
        }
        List<RemoteConfig> configs = new ArrayList<>();
        RemoteConfig yml = new RemoteConfig();
        yml.setNamespace(configFiles.get(0).getNamespace());
        yml.setPriority(-2);
        yml.setType(ConfigType.YAML);
        yml.setName(configFiles.get(0).getName());
        yml.setApplicationCode(PathParser.urlDecode(applicationCode));
        yml.setContent("people: \n" +
                "  age: " + age + "\n" +
                "  gender: " + getGender() + "\n" +
                "  company:\n" +
                "    address: " + getCompanyAddress() + "\n" +
                "    name: " + getCompanyName() + "\n" +
                "  name: " + getName() + "\n"
        );
        configs.add(yml);
        if (configFiles.size() > 1) {
            RemoteConfig property = new RemoteConfig();
            property.setNamespace(configFiles.get(1).getNamespace());
            property.setPriority(-3);
            property.setType(ConfigType.PROPERTIES);
            property.setName(configFiles.get(1).getName());
            property.setApplicationCode(PathParser.urlDecode(applicationCode));
            property.setContent("global.age=" + getAge());
            configs.add(property);
        }
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

    public String getGender() {
        return gender;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public static ConfigServiceImpl getInstance() {
        return inst;
    }
}
