package rabbit.discovery.api.test.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.Privilege;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.common.enums.ConfigType;
import rabbit.discovery.api.common.protocol.ApplicationInstance;
import rabbit.discovery.api.common.protocol.ApplicationMeta;
import rabbit.discovery.api.common.protocol.ClusterInstanceMeta;
import rabbit.discovery.api.common.protocol.RegisterResult;
import rabbit.discovery.api.common.rpc.ApiData;
import rabbit.discovery.api.common.rpc.HttpProtocolService;
import rabbit.discovery.api.common.utils.PathParser;
import rabbit.discovery.api.test.spi.ApiCache;
import rabbit.flt.common.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static rabbit.discovery.api.common.utils.PathParser.urlDecode;

public class DiscoveryServiceImpl implements HttpProtocolService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final DiscoveryServiceImpl inst = new DiscoveryServiceImpl();

    private int port = 1802;

    private ApplicationMeta applicationMeta;

    private DiscoveryServiceImpl() {
        applicationMeta = new ApplicationMeta();
        applicationMeta.setPrivilegeVersion(1L);
        applicationMeta.setRegistryAddressVersion(1L);
        applicationMeta.setPrivilegeVersion(1L);
    }

    @Override
    public RegisterResult register(ApplicationInstance instance) {
        logger.info("application[{}] instance[{}:{}] register success!", instance.getApplicationCode(),
                instance.getHost(), instance.getPort());
        applicationMeta.getClusterMetas().computeIfAbsent("restApiSampleServer", k -> {
            ClusterInstanceMeta meta = new ClusterInstanceMeta();
            meta.addClusterLoadBalance("default", "http://localhost:1802");
            meta.addClusterLoadBalance("local", "http://127.0.0.1:1802");
            return meta;
        });
        return RegisterResult.success(applicationMeta);
    }

    @Override
    public RegisterResult keepAlive(ApplicationInstance instance) {
        logger.info("application[{}] instance[{}:{}] keepAlive success!", instance.getApplicationCode(),
                instance.getHost(), instance.getPort());
        return RegisterResult.success(applicationMeta);
    }

    @Override
    public String getPublicKey(String applicationCode) {
        logger.info("load app[{}] public key success!", urlDecode(applicationCode));
        String publicKey = "305C300D06092A864886F70D0101010500034B003048024100C5B76A3974FEED9144066469D95D3A0297288F626A54A3624901552353DFBDA20FA4156CE11C6048FC3F9DB79101DB047933E031074719C10D552E05658D16290203010001";
        return publicKey;
    }

    @Override
    public String getRegistryAddress() {
        logger.info("load registry address success!");
        return "localhost:".concat(Integer.toString(port));
    }

    @Override
    public List<Privilege> getProviderPrivileges(String applicationCode) {
        String appCode = urlDecode(applicationCode);
        logger.info("application[{}] load privilege data success!", appCode);
        List<Privilege> list = new ArrayList<>();
        Privilege data = new Privilege();
        data.setProvider(appCode);
        data.setConsumer(appCode);
        data.setPath("/black/authorized");
        list.add(data);
        return list;
    }

    private long configVersion = 0L;

    private int age = 10;

    private String name = "zhang3";

    private String gender = "male";

    private String companyName = "alibaba";

    private String companyAddress = "chengdu";

    @Override
    public List<RemoteConfig> loadConfig(String applicationCode, List<RemoteConfig> configFiles) {
        if (CollectionUtils.isEmpty(configFiles)) {
            return new ArrayList<>();
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
        return configs;
    }

    @Override
    public void doReport(String applicationCode, ApiData apiData) {
        ApiCache.getMap().put(apiData.getClassName(), apiData.getApiList());
    }

    /**
     * 设置并刷新配置版本
     *
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

    public void incrementConfigVersion() {
        applicationMeta.setConfigVersion(this.applicationMeta.getConfigVersion() + 1);
    }

    public static DiscoveryServiceImpl getInstance() {
        return inst;
    }
}
