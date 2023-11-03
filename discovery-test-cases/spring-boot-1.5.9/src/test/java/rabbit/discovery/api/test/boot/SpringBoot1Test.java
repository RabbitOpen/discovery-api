package rabbit.discovery.api.test.boot;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import rabbit.discovery.api.test.CoreCases;
import rabbit.discovery.api.test.HttpRequestInterceptor;
import rabbit.discovery.api.test.TestLoadBalancer;
import rabbit.discovery.api.test.bean.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBoot1Entry.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import({TestLoadBalancer.class, HttpRequestInterceptor.class})
public class SpringBoot1Test {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    FeignServiceClient feignServiceClient;

    @Test
    public void springBoot1Test() throws Exception {
        TestLoadBalancer balancer = applicationContext.getBean(TestLoadBalancer.class);
        balancer.setPort(1802);
        CoreCases cases = new CoreCases();
        cases.configLoadCase(applicationContext);
        cases.openApiCase(applicationContext);
        cases.restApiCase(applicationContext);
        cases.reportServiceCase();
        cases.authorizationUrlCase(applicationContext);
        cases.springMvcTraceEnhanced();
        runOpenFeignCase();
    }

    private void runOpenFeignCase() {
        String name = "zhang3";
        int age = 12;
        User user = feignServiceClient.getUser(name, 123, new User(name, age));
        TestCase.assertEquals(name, user.getName());
        TestCase.assertEquals(123, user.getAge());
    }
}


