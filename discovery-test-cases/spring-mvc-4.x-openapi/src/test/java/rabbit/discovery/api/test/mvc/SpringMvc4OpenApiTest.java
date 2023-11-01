package rabbit.discovery.api.test.mvc;

import org.apache.catalina.LifecycleException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import rabbit.discovery.api.test.CoreCases;

import javax.servlet.ServletException;
import java.io.IOException;

@RunWith(JUnit4.class)
public class SpringMvc4OpenApiTest {

    /**
     * discovery-rest 可单独作为api和open api的客户端使用
     * 由于没有Discovery能力，所以需要自己实现LoadBalancer
     */
    @Test
    public void springMvc4OpenApiTest() throws ServletException, LifecycleException, IOException {
        new TomcatContainer(1802).execute(ctx -> {
            CoreCases coreCases = new CoreCases();
            coreCases.openApiCase(ctx);
            coreCases.restApiCase(ctx);
        });
    }



}
