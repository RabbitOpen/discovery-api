package rabbit.discovery.api.starter.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import rabbit.discovery.api.common.Framework;
import rabbit.discovery.api.starter.ClassUtils;

/**
 * spring boot 启动入口 该类通过 spring.factories初始化
 */
public class SpringBootStartListener implements ApplicationListener, Ordered {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        Framework.setFrameWork(Framework.SPRING_BOOT);
        ClassUtils.doProxy();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
