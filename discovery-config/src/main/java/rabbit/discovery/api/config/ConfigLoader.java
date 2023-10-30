package rabbit.discovery.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.*;
import rabbit.discovery.api.common.enums.ConfigType;
import rabbit.discovery.api.common.exception.ConfigException;
import rabbit.discovery.api.common.spi.ConfigChangeListener;
import rabbit.discovery.api.common.utils.RsaUtils;
import rabbit.discovery.api.config.anno.FlexibleBean;
import rabbit.discovery.api.config.anno.FlexibleValue;
import rabbit.discovery.api.config.context.FlexibleValueMeta;
import rabbit.discovery.api.config.reader.PropertyReader;
import rabbit.discovery.api.config.reader.YamlReader;
import rabbit.discovery.api.config.rpc.ConfigService;
import rabbit.flt.common.utils.CollectionUtils;
import rabbit.flt.common.utils.ReflectUtils;
import rabbit.flt.common.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static rabbit.discovery.api.common.ApiProtocolHelper.getSignatureMap;
import static rabbit.flt.common.utils.StringUtils.isEmpty;

/**
 * 配置加载器
 */
public abstract class ConfigLoader extends Thread implements ConfigChangeListener {

    private Logger logger = LoggerFactory.getLogger(getName());

    private ArrayBlockingQueue<Long> task = new ArrayBlockingQueue<>(64);

    /**
     * 注册中心地址
     */
    protected String registryAddress;

    protected String applicationCode;

    // 配置加载服务
    protected ConfigService configService;

    protected PrivateKey privateKey;

    /**
     * 配置详情
     */
    protected ConfigDetail currentConfig;

    /**
     * 本地配置的描述
     */
    protected final List<RemoteConfig> configFiles = new ArrayList<>();

    protected static final Map<ConfigType, ConfigReader> readerCache = new EnumMap(ConfigType.class);

    protected static final Map<Object, FlexibleValueMeta> beanMeta = new ConcurrentHashMap<>();

    static {
        readerCache.put(ConfigType.PROPERTIES, new PropertyReader());
        readerCache.put(ConfigType.YAML, new YamlReader());
    }

    public ConfigLoader() {
        setName("remote-config-loader");
        setDaemon(true);
    }

    /**
     * 获取匹配的框架
     *
     * @return
     */
    public abstract Framework getCompatibleFramework();

    /**
     * 读属性值
     *
     * @param propertyName
     * @return
     */
    public abstract String readProperty(String propertyName);

    /**
     * 读值
     *
     * @param propertyName
     * @param defaultValue 默认返回值
     * @return
     */
    protected abstract String readProperty(String propertyName, String defaultValue);

    /**
     * 添加配置
     *
     * @param configs
     */
    public abstract void addPropertySources(List<RemoteConfig> configs);

    /**
     * 更新配置
     *
     * @param configs
     */
    protected abstract void updatePropertySources(List<RemoteConfig> configs);

    /**
     * 读取远程配置
     *
     * @return
     */
    public synchronized ConfigDetail loadRemoteConfig() {
        if (configFiles.isEmpty()) {
            currentConfig = new ConfigDetail();
        } else {
            currentConfig = configService.loadConfig(applicationCode, configFiles, getSignatureMap(applicationCode, privateKey));
            List<RemoteConfig> configs = currentConfig.getConfigs();
            if (!CollectionUtils.isEmpty(configs)) {
                configs.forEach(c -> c.setPriority(getPriority(c)));
                configs.sort(Comparator.comparing(RemoteConfig::getPriority));
            }
        }
        return currentConfig;
    }

    private Integer getPriority(RemoteConfig c) {
        Optional<RemoteConfig> first = configFiles.stream().filter(c1 -> Objects.equals(c1.getNamespace(), c.getNamespace())
                && Objects.equals(c1.getName(), c.getName())).findFirst();
        if (!first.isPresent()) {
            throw new ConfigException(String.format("config[%s] is not existed", c.getName()));
        }
        return first.get().getPriority();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Long version = task.poll(1, TimeUnit.SECONDS);
                if (null == version) {
                    continue;
                }
                if (null == currentConfig || null == currentConfig.getVersion() ||
                        currentConfig.getVersion() < version) {
                    task.clear();
                    loadRemoteConfig();
                    if (!CollectionUtils.isEmpty(currentConfig.getConfigs())) {
                        this.updatePropertySources(currentConfig.getConfigs());
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 读取自定义值处理器
     *
     * @return
     */
    protected PropertyHandler getPropertyHandler() {
        String handlerClz = readProperty("discovery.config.handler");
        if (isEmpty(handlerClz)) {
            return (key, value) -> value;
        }
        try {
            Class clz = Class.forName(handlerClz);
            if (!PropertyHandler.class.isAssignableFrom(clz)) {
                throw new ConfigException("class[".concat(handlerClz).concat("]未实现接口[")
                        .concat(PropertyHandler.class.getName()).concat("]"));
            }
            return (PropertyHandler) clz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException e) {
            throw new ConfigException(e);
        }
    }

    /**
     * 获取配置名
     *
     * @param config
     * @return
     */
    protected String getConfigName(RemoteConfig config) {
        return config.getApplicationCode().concat("@").concat(config.getNamespace())
                .concat("@").concat(config.getName());
    }

    /**
     * 为spring bean注入属性
     *
     * @param bean
     * @param valueChangeListener
     */
    public final void injectProperty(Object bean, ValueChangeListener valueChangeListener) {
        if (hasFlexibleValueProperty(bean)) {
            FlexibleValueMeta meta = new FlexibleValueMeta(bean, valueChangeListener);
            beanMeta.put(bean, meta);
            meta.inject(this::readProperty);
        }
        getFlexibleBeanFields(bean).forEach((field, flexibleBean) -> {
            Object value = ReflectUtils.newInstance(field.getType());
            FlexibleValueMeta meta = new FlexibleValueMeta(value, valueChangeListener);
            meta.resolvePropertyMeta(flexibleBean.propertyPrefix());
            meta.setUpdatable(flexibleBean.updatable());
            beanMeta.put(value, meta);
            meta.inject(this::readProperty);
            ReflectUtils.setValue(bean, field, value);
        });
    }

    private Map<Field, FlexibleBean> getFlexibleBeanFields(Object bean) {
        Map<Field, FlexibleBean> fields = new HashMap<>();
        Class<?> clz = bean.getClass();
        while (true) {
            for (Field field : clz.getDeclaredFields()) {
                FlexibleBean fb = field.getAnnotation(FlexibleBean.class);
                if (null != fb) {
                    fields.put(field, fb);
                }
            }
            if (clz.getSuperclass() == Object.class) {
                break;
            }
            clz = clz.getSuperclass();
        }
        return fields;
    }

    /**
     * 判断是否包含@FlexibleValue注解的属性
     *
     * @param bean
     * @return
     */
    private boolean hasFlexibleValueProperty(Object bean) {
        Class<?> clz = bean.getClass();
        while (true) {
            for (Field field : clz.getDeclaredFields()) {
                if (null != field.getAnnotation(FlexibleValue.class)) {
                    return true;
                }
            }
            if (Object.class == clz.getSuperclass()) {
                break;
            }
            clz = clz.getSuperclass();
        }
        return false;
    }

    @Override
    public void updateConfig(Long version) {
        if (null == version) {
            return;
        }
        task.add(version);
    }

    public void init() {
        if (null == configService) {
            readConfigs();
            registryAddress = readProperty("discovery.registry.address");
            Configuration configuration = new Configuration();
            configuration.setRegistryAddress(registryAddress);
            configService = RequestFactory.proxy(ConfigService.class, configuration);
            applicationCode = readProperty("discovery.application.code");
            String propertyName = "discovery.application.security.key";
            privateKey = RsaUtils.loadPrivateKeyFromString(readProperty(propertyName));
        }
    }

    /**
     * 从配置文件中获取配置信息
     */
    private void readConfigs() {
        String max = readProperty("discovery.config.max", "16");
        for (int i = 0; i < Integer.parseInt(max); i++) {
            RemoteConfig rc = readConfig(i);
            if (null == rc) {
                continue;
            }
            configFiles.add(rc);
        }
    }

    private RemoteConfig readConfig(int i) {
        String prefix = "discovery.config.items[".concat(Integer.toString(i)).concat("].");
        String name = readProperty(prefix.concat("name"));
        String namespace = readProperty(prefix.concat("namespace"));
        if (null == name || null == namespace) {
            return null;
        }
        RemoteConfig rc = new RemoteConfig();
        rc.setName(name);
        rc.setNamespace(namespace);
        String priority = readProperty(prefix.concat("priority"));
        if (StringUtils.isEmpty(priority)) {
            rc.setPriority(1);
        } else {
            rc.setPriority(Integer.parseInt(priority.trim()));
        }
        return rc;
    }

    @Override
    public synchronized void start() {
        if (isAlive()) {
            return;
        }
        super.start();
    }
}
