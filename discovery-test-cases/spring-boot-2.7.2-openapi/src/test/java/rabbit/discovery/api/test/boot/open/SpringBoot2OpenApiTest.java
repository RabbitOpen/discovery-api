package rabbit.discovery.api.test.boot.open;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import rabbit.discovery.api.test.CoreCases;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBoot2Entry.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SpringBoot2OpenApiTest {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * discovery-rest 可单独作为api和open api的客户端使用
     * 由于没有Discovery能力，所以需要自己实现LoadBalancer
     */
    @Test
    public void springBoot2OpenApiTest() {
        CoreCases cases = new CoreCases();
        cases.openApiCase(applicationContext);
        cases.restApiCase(applicationContext);
    }
}


