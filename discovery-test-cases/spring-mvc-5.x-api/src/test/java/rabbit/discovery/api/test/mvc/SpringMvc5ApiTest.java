package rabbit.discovery.api.test.mvc;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import rabbit.discovery.api.test.CoreCases;
import rabbit.discovery.api.test.TestLoadBalancer;
import rabbit.discovery.api.test.TomcatContainer;

import javax.servlet.ServletException;
import java.io.IOException;

@RunWith(JUnit4.class)
public class SpringMvc5ApiTest {

    /**
     * discovery-rest 可单独作为api和open api的客户端使用
     * 由于没有Discovery能力，所以需要自己实现LoadBalancer 或者 自己启动DiscoveryService
     */
    @Test
    public void springMvc5ApiTest() throws ServletException, LifecycleException, IOException {
        new TomcatContainer(1802) {
            @Override
            protected void addServletListener(Context context) {
                // do nothing
            }
        }.execute(ctx -> {
            TestLoadBalancer balancer = ctx.getBean(TestLoadBalancer.class);
            balancer.setPort(1802);
            CoreCases cases = new CoreCases();
            cases.openApiCase(ctx);
            cases.restApiCase(ctx);
            cases.simpleDataTest(ctx);
        });
    }



}
