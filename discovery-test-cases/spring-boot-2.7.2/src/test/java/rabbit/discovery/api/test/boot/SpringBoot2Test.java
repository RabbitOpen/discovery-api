package rabbit.discovery.api.test.boot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import rabbit.discovery.api.test.CoreCases;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBoot2Entry.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SpringBoot2Test {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void springBoot2Test() throws Exception {
        TestLoadBalancer balancer = applicationContext.getBean(TestLoadBalancer.class);
        balancer.setPort(1802);

        CoreCases cases = new CoreCases();
        cases.configLoadCase(applicationContext);
        cases.openApiCase(applicationContext);
        cases.restApiCase(applicationContext);
        cases.reportServiceCase();
        cases.authorizationUrlCase(applicationContext);

    }
}


