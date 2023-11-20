package rabbit.discovery.api.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.global.ApplicationMetaCache;
import rabbit.discovery.api.common.global.AuthorizationDetail;
import rabbit.discovery.api.common.protocol.ApplicationInstance;
import rabbit.discovery.api.common.protocol.ApplicationMeta;
import rabbit.discovery.api.common.protocol.RegisterResult;
import rabbit.discovery.api.common.rpc.ProtocolServiceWrapper;
import rabbit.discovery.api.common.spi.ConfigChangeListener;
import rabbit.discovery.api.common.utils.PathParser;
import rabbit.discovery.api.common.utils.PathPattern;
import rabbit.flt.common.Metrics;
import rabbit.flt.common.utils.CollectionUtils;
import rabbit.flt.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

/**
 * 入口
 */
public class DefaultDiscoveryService implements DiscoveryService {

    private Logger logger = LoggerFactory.getLogger("discoveryService");

    private Configuration configuration;

    private ApplicationInstance instance;

    private long authorizationVersion = -2L;

    /**
     * 发生错误
     */
    private boolean errorFound = false;

    /**
     * 标记已经注册过
     */
    private boolean registered = false;

    @Override
    public void start() {
        initApplicationInstance();
        registerAndSynchronizeData();
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    LockSupport.parkNanos(3L * 1000 * 1000 * 1000);
                    registerAndSynchronizeData();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    /**
     * 注册并同步数据
     */
    private void registerAndSynchronizeData() {
        try {
            ApplicationMeta meta = this.loadLatestApplicationMeta();
            updateRegistryAddress(meta.getRegistryAddressVersion());
            updateConfig(meta.getConfigVersion());
            if (authorizationVersion != meta.getPrivilegeVersion()) {
                loadProviderPrivileges();
                logger.info("application authorization data is updated!");
                authorizationVersion = meta.getPrivilegeVersion();
            }
            if (errorFound) {
                errorFound = false;
                logger.info("communication is recovered!");
            }
            ApplicationMetaCache.setApplicationMeta(meta);
        } catch (DiscoveryException e) {
            errorFound = true;
            logger.warn(e.getMessage());
        } catch (Exception e) {
            errorFound = true;
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 加载自己的授权
     */
    private void loadProviderPrivileges() {
        List<Privilege> list = ProtocolServiceWrapper.getProviderPrivileges(configuration.getApplicationCode());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Map<String, List<PathPattern>> privilegeMap = new ConcurrentHashMap<>();
        for (Privilege p : list) {
            privilegeMap.computeIfAbsent(p.getConsumer(), c -> new ArrayList<>())
                    .add(PathParser.parsePattern(p.getPath()));
        }
        AuthorizationDetail.setAuthorizationDetails(privilegeMap);
    }

    private void updateConfig(Long configVersion) {
        ServiceLoader<ConfigChangeListener> loader = ServiceLoader.load(ConfigChangeListener.class);
        loader.forEach(l -> l.updateConfig(configVersion));
    }

    private void updateRegistryAddress(Long registryVersion) {
        long currentVersion = ApplicationMetaCache.getApplicationMeta().getRegistryAddressVersion().longValue();
        if (currentVersion != registryVersion.longValue()) {
            configuration.setRegistryAddress(ProtocolServiceWrapper.getRegistryAddress());
            logger.info("registry address is updated! current address is {}", configuration.getRegistryAddress());
        }
    }

    /**
     * 刷新meta
     *
     * @return
     */
    private ApplicationMeta loadLatestApplicationMeta() {
        RegisterResult result;
        if (!registered) {
            result = ProtocolServiceWrapper.register(instance);
            if (result.isSuccess()) {
                logger.info("应用[{}.{}]注册成功", instance.getApplicationCode(), instance.getClusterName());
                registered = true;
                return result.getApplicationMeta();
            }
        } else {
            result = ProtocolServiceWrapper.keepAlive(instance);
            if (result.isSuccess()) {
                return result.getApplicationMeta();
            }
        }
        throw new DiscoveryException(result.getMessage());
    }

    private void initApplicationInstance() {
        instance = new ApplicationInstance(configuration.getApplicationCode());
        instance.setPort(configuration.getPort());
        instance.setClusterName(configuration.getClusterName());
        if (!StringUtils.isEmpty(configuration.getHost())) {
            instance.setHost(configuration.getHost());
        } else {
            instance.setHost(Metrics.getHostIp());
        }
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        ProtocolServiceWrapper.init(configuration);
    }

}
