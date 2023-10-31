package rabbit.discovery.api.test.cloud2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import rabbit.discovery.api.test.CoreCases;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringCloud2Entry.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SpringCloud2Test {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void springCloud2Test() throws Exception {
        CoreCases cases = new CoreCases();
        cases.configLoadCase(applicationContext);
        cases.openApiCase(applicationContext);
    }
}


