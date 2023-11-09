package rabbit.discovery.api.common;

import com.fasterxml.jackson.databind.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.global.ApplicationMetaCache;
import rabbit.discovery.api.common.global.AuthorizationDetail;
import rabbit.discovery.api.common.protocol.ApplicationInstance;
import rabbit.discovery.api.common.protocol.ApplicationMeta;
import rabbit.discovery.api.common.protocol.PrivilegeData;
import rabbit.discovery.api.common.protocol.RegisterResult;
import rabbit.discovery.api.common.rpc.ProtocolService;
import rabbit.discovery.api.common.spi.ConfigChangeListener;
import rabbit.discovery.api.common.utils.JsonUtils;
import rabbit.discovery.api.common.utils.PathParser;
import rabbit.discovery.api.common.utils.PathPattern;
import rabbit.flt.common.Metrics;
import rabbit.flt.common.utils.GZipUtils;
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

    private ProtocolService protocolService;

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
        protocolService = getProtocolService();
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

    private ProtocolService getProtocolService() {
        if (CommunicationMode.HTTP == configuration.getCommunicationMode()) {
            return RequestFactory.proxy(ProtocolService.class, configuration);
        } else{
            RpcFactory.init(configuration);
            return RpcFactory.proxy(ProtocolService.class);
        }
    }

    /**
     * 注册并同步数据
     */
    private void registerAndSynchronizeData() {
        try {
            ApplicationMeta meta = this.loadLatestApplicationMeta();
            updateRegistryAddress(meta.getRegistryAddressVersion());
            updateConfig(meta.getConfigVersion());
            if (authorizationVersion != meta.getAuthVersion()) {
                loadProviderPrivileges();
                logger.info("application authorization data is updated!");
                authorizationVersion = meta.getAuthVersion();
            }
            if (errorFound) {
                errorFound = false;
                logger.info("communication is recovered!");
            }
            BeanUtils.copyProperties(meta, ApplicationMetaCache.getApplicationMeta());
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
        PrivilegeData data = protocolService.getProviderPrivileges(configuration.getApplicationCode());
        if (0 == data.getPlainDataLength()) {
            return;
        }
        byte[] bytes = GZipUtils.decompress(data.getCompressedPrivileges(), data.getPlainDataLength());
        JavaType javaType = JsonUtils.constructListType(ArrayList.class, Privilege.class);
        List<Privilege> privileges = JsonUtils.readValue(new String(bytes), javaType);
        Map<String, List<PathPattern>> privilegeMap = new ConcurrentHashMap<>();
        for (Privilege p : privileges) {
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
        if (currentVersion != registryVersion.byteValue()) {
            configuration.setRegistryAddress(protocolService.getRegistryAddress());
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
            result = protocolService.register(instance);
            if (result.isSuccess()) {
                instance.setId(result.getId());
                logger.info("应用[{}.{}]注册成功，实例id: {}", instance.getApplicationCode(), instance.getClusterName(), instance.getId());
                registered = true;
                return result.getApplicationMeta();
            }
        } else {
            result = protocolService.keepAlive(instance);
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
    }

}
