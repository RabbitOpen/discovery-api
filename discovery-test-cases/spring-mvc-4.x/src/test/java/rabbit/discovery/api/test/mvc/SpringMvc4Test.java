package rabbit.discovery.api.test.mvc;

import org.apache.catalina.LifecycleException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import rabbit.discovery.api.test.CoreCases;
import rabbit.discovery.api.test.TestLoadBalancer;
import rabbit.discovery.api.test.TomcatContainer;
import rabbit.discovery.api.test.controller.DiscoveryController;
import rabbit.discovery.api.test.spi.MySpringMvcConfigLoader;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.concurrent.Semaphore;

@RunWith(JUnit4.class)
public class SpringMvc4Test {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void springMvc4Test() throws ServletException, LifecycleException, IOException {
        new TomcatContainer(1802).execute(ctx -> {
            try {
                TestLoadBalancer balancer = ctx.getBean(TestLoadBalancer.class);
                balancer.setPort(1802);
                CoreCases cases = new CoreCases() {
                    @Override
                    protected Semaphore createHoldOnSemaphore(ApplicationContext context) {
                        Semaphore semaphore = new Semaphore(0);
                        DiscoveryController discoveryController = context.getBean(DiscoveryController.class);
                        // 刷新配置版本号
                        discoveryController.incrementConfigVersion();
                        // spring mvc的config loader与spring boot不是同一个
                        MySpringMvcConfigLoader.setCallBack(semaphore::release);
                        return semaphore;
                    }
                };
                cases.configLoadCase(ctx);
                cases.openApiCase(ctx);
                cases.restApiCase(ctx);
                cases.reportServiceCase();
                cases.authorizationUrlCase(ctx);
                cases.springMvcTraceEnhanced();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });
    }



}
