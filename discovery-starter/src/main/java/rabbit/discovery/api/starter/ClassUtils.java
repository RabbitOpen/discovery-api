package rabbit.discovery.api.starter;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import rabbit.discovery.api.common.SpringBeanSupplier;
import rabbit.discovery.api.plugins.common.Matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

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
    }

    /**
     * 获取bean supplier
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
}
