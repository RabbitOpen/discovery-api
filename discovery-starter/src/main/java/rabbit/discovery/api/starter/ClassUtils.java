package rabbit.discovery.api.starter;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.SpringBeanSupplier;
import rabbit.discovery.api.common.TraceConfiguration;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.plugins.common.Matcher;
import rabbit.flt.common.AbstractConfigFactory;
import rabbit.flt.common.AgentConfig;
import rabbit.flt.common.log.AgentLoggerFactory;
import rabbit.flt.common.utils.ResourceUtils;
import rabbit.flt.common.utils.StringUtils;
import rabbit.flt.core.AgentHelper;

import java.io.InputStream;
import java.util.*;

/**
 * class增强工具
 */
public final class ClassUtils {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 唯一实例
     */
    private static final ClassUtils inst = new ClassUtils();

    // 初始化状态
    private boolean initialized = false;

    // 外部设置
    private ApplicationContext context;

    private ClassUtils() {
    }

    /**
     * 设置context
     *
     * @param context
     */
    public static void setContext(ApplicationContext context) {
        inst.context = context;
    }

    /**
     * 代理增强
     */
    public static synchronized void doProxy() {
        inst.transformClassFiles();
    }

    /**
     * 转换class
     */
    private void transformClassFiles() {
        if (initialized) {
            return;
        }
        initialized = true;
        ByteBuddyAgent.install();
        ServiceLoader<Matcher> matchers = ServiceLoader.load(Matcher.class);
        AgentBuilder.Default builder = new AgentBuilder.Default();
        ElementMatcher.Junction<TypeDescription> classMatcher = null;
        List<Matcher> all = new ArrayList<>();
        for (Matcher matcher : matchers) {
            if (null == classMatcher) {
                classMatcher = matcher.classMatcher();
            } else {
                classMatcher = classMatcher.or((matcher.classMatcher()));
            }
            all.add(matcher);
        }
        builder.type(classMatcher).transform(new DiscoveryClassTransformer(all, getSupplier()))
                .installOnByteBuddyAgent();
        initTraceAgent();
    }

    /**
     * 初始化链路追踪
     */
    private void initTraceAgent() {
        TraceConfiguration traceConfig = readConfigFromFile();
        if (null != traceConfig && traceConfig.isFltDisabled()) {
            return;
        }
        initLoggerFactory();
        AbstractConfigFactory.setFactoryLoader(() -> new AbstractConfigFactory() {
            @Override
            public void initialize() {
                // do nothing
            }

            @Override
            protected AgentConfig getAgentConfig() {
                TraceConfiguration tc = getBean(TraceConfiguration.class);
                if (null == tc) {
                    return traceConfig;
                }
                if (StringUtils.isEmpty(tc.getServers())) {
                    throw new DiscoveryException("agent server 不能为空");
                }
                Configuration config = getBean(Configuration.class);
                if (null != config && StringUtils.isEmpty(tc.getSecurityKey())) {
                    tc.setSecurityKey(config.getPrivateKey().substring(0, 16));
                }
                return tc;
            }
        });
        AgentHelper.installPlugins(ByteBuddyAgent.getInstrumentation());
    }

    /**
     * 获取bean supplier
     *
     * @return
     */
    public static SpringBeanSupplier getSupplier() {
        return new SpringBeanSupplier() {
            @Override
            public <T> T getSpringBean(Class<T> clz) {
                return inst.getBean(clz);
            }

            @Override
            public <T> Collection<T> getSpringBeans(Class<T> clz) {
                return inst.getBeans(clz);
            }
        };
    }

    /**
     * 获取定义的bean
     *
     * @param clz
     * @param <T>
     * @return
     */
    private <T> T getBean(Class<T> clz) {
        try {
            return context.getBean(clz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取定义的beans
     *
     * @param clz
     * @param <T>
     * @return
     */
    private <T> Collection<T> getBeans(Class<T> clz) {
        try {
            return context.getBeansOfType(clz).values();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 初始化日志工厂
     */
    private void initLoggerFactory() {
        AgentLoggerFactory.setFactory(new rabbit.flt.common.log.LoggerFactory() {
            @Override
            public rabbit.flt.common.log.Logger getLogger(String s) {
                return new LoggerProxy(LoggerFactory.getLogger(s));
            }

            @Override
            public rabbit.flt.common.log.Logger getLogger(Class<?> clz) {
                return new LoggerProxy(LoggerFactory.getLogger(clz));
            }
        });
    }

    /**
     * 从agent.properties文件读取配置
     *
     * @return
     */
    private TraceConfiguration readConfigFromFile() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("agent.properties");
        try {
            TraceConfiguration config = new TraceConfiguration();
            Properties properties = new Properties();
            properties.load(stream);
            config.setIgnoreClasses(properties.getProperty("discovery.application.flt.ignoreClasses"));
            config.setIgnorePackages(properties.getProperty("discovery.application.flt.ignorePackages"));
            boolean fltDisabled = Boolean.parseBoolean(properties.getProperty("discovery.application.flt.disabled", "false"));
            config.setFltDisabled(fltDisabled);
            return config;
        } catch (Exception e) {
            return null;
        } finally {
            ResourceUtils.close(stream);
        }
    }
}
