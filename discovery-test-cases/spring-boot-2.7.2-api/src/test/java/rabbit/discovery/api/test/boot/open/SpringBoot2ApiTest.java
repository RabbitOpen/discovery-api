package rabbit.discovery.api.test.boot.open;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.DefaultDiscoveryService;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.utils.JsonUtils;
import rabbit.discovery.api.test.CoreCases;
import rabbit.discovery.api.test.HttpRequestInterceptor;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBoot2Entry.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import({HttpRequestInterceptor.class})
public class SpringBoot2ApiTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Configuration configuration;

    /**
     * discovery-rest 可单独作为api和open api的客户端使用
     * 由于没有Discovery能力，所以需要自己实现LoadBalancer
     *  或者 自己启动DiscoveryService
     */
    @Test
    public void springBoot2ApiTest() {
        DefaultDiscoveryService discoveryService = new DefaultDiscoveryService();
        discoveryService.setConfiguration(configuration);
        discoveryService.start();
        CoreCases cases = new CoreCases();
        cases.openApiCase(applicationContext);
        cases.restApiCase(applicationContext);
        cases.simpleDataTest(applicationContext);
        cases.retryCase(applicationContext);
    }

    @Test
    public void configTest() {
        String json = JsonUtils.writeObject(configuration);
        try {
            Configuration c = JsonUtils.readValue(json, Configuration.class);
            c.setRegistryAddress("");
            c.doValidation();
            throw new RuntimeException("");
        } catch (DiscoveryException e) {
            TestCase.assertTrue(e.getMessage().contains("注册中心地址信息不能为空"));
        }

        try {
            Configuration c = JsonUtils.readValue(json, Configuration.class);
            c.setApplicationCode("");
            c.doValidation();
            throw new RuntimeException("");
        } catch (DiscoveryException e) {
            TestCase.assertTrue(e.getMessage().contains("应用编码信息不能为空"));
        }

        try {
            Configuration c = JsonUtils.readValue(json, Configuration.class);
            c.setPrivateKey("");
            c.doValidation();
            throw new RuntimeException("");
        } catch (DiscoveryException e) {
            TestCase.assertTrue(e.getMessage().contains("应用密钥信息不能为空"));
        }
        try {
            Configuration c = JsonUtils.readValue(json, Configuration.class);
            c.setPort(-1);
            c.setServerPort(-1);
            c.doValidation();
            throw new RuntimeException("");
        } catch (DiscoveryException e) {
            TestCase.assertTrue(e.getMessage().contains("应用端口信息不能为空"));
        }
    }
}


