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

    @Test
    public void springBoot2OpenApiTest() throws Exception {
        CoreCases cases = new CoreCases();
        cases.openApiCase(applicationContext);
    }
}


