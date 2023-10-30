package rabbit.discovery.api.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import rabbit.discovery.api.common.DefaultDiscoveryService;
import rabbit.discovery.api.common.DiscoveryService;
import rabbit.discovery.api.common.exception.DiscoveryException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;

/**
 * 互斥的starter， 子类当中只能有一个被注册
 */
abstract class MutuallyExclusiveStarter implements ApplicationContextAware {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        setContext(applicationContext);
        String[] names = applicationContext.getBeanNamesForType(MutuallyExclusiveStarter.class);
        if (names.length > 1) {
            throw new DiscoveryException("multi starter exception");
        }
    }

    private static void setContext(ApplicationContext context) {
        MutuallyExclusiveStarter.context = context;
    }

    /**
     * 获取定义的bean
     * @param clz
     * @param <T>
     * @return
     */
    protected <T> T getBean(Class<T> clz) {
        try {
            return getContext().getBean(clz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取定义的beans
     * @param clz
     * @param <T>
     * @return
     */
    protected <T> Collection<T> getBeans(Class<T> clz) {
        try {
            return getContext().getBeansOfType(clz).values();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * 获取通信协议服务
     * @return
     */
    protected DiscoveryService getDiscoveryService() {
        DiscoveryService discoveryService = null;
        for (DiscoveryService ds : ServiceLoader.load(DiscoveryService.class)) {
            if (null == discoveryService) {
                discoveryService = ds;
                continue;
            }
            if (ds.getPriority() < discoveryService.getPriority()) {
                discoveryService = ds;
            }
        }
        if (null == discoveryService) {
             discoveryService = new DefaultDiscoveryService();
        }
        logger.info("running with discoveryService[{}]", discoveryService.getClass().getName());
        return discoveryService;
    }
}
