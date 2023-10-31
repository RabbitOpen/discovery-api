package rabbit.discovery.api.test;

import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import rabbit.discovery.api.common.Headers;
import rabbit.discovery.api.rest.http.HttpResponse;
import rabbit.discovery.api.test.bean.People;
import rabbit.discovery.api.test.bean.User;
import rabbit.discovery.api.test.controller.ConfigController;
import rabbit.discovery.api.test.controller.DiscoveryController;
import rabbit.discovery.api.test.open.OpenApiSample;
import rabbit.discovery.api.test.spi.MySpringBootConfigLoader;

import java.util.concurrent.Semaphore;

/**
 * 核心用例
 */
public class CoreCases {


    /**
     * 加载远程配置case
     * @param context
     */
    public void configLoadCase(ApplicationContext context) throws Exception {
        People people = context.getBean(People.class);
        ConfigController controller = context.getBean(ConfigController.class);
        TestCase.assertEquals(controller.getAge(), people.getAge());
        TestCase.assertEquals(controller.getName(), people.getName());
        TestCase.assertEquals(controller.getGender(), people.getGender());
        TestCase.assertEquals(controller.getCompanyName(), people.getCompanyObj().getName());
        TestCase.assertEquals(controller.getCompanyAddress(), people.getCompanyObj().getAddress());
        Semaphore semaphore = createHoldOnSemaphore(context);
        controller.update(12, "alipay");
        semaphore.acquire();
        TestCase.assertEquals(12, people.getAge());
        TestCase.assertEquals("alipay", people.getCompanyObj().getName());
    }

    /**
     * open api调用示例
     * @param context
     */
    public void openApiCase(ApplicationContext context) {
        OpenApiSample apiSample = context.getBean(OpenApiSample.class);
        String name = "zhang3";
        int age = 12;
        HttpResponse<User> response = apiSample.createUser(name, age);
        TestCase.assertEquals(name, response.getData().getName());
        TestCase.assertEquals(age, response.getData().getAge());
        TestCase.assertEquals("c1", response.getHeaders().get(Headers.OPEN_API_CODE.toLowerCase()));
        TestCase.assertEquals("c1", response.getHeaders().get(Headers.OPEN_API_CREDENTIAL.toLowerCase()));
        TestCase.assertTrue(response.getHeaders().containsKey(Headers.OPEN_API_REQUEST_TIME.toLowerCase()));
        TestCase.assertTrue(response.getHeaders().containsKey(Headers.OPEN_API_REQUEST_TIME_SIGNATURE.toLowerCase()));
        response = apiSample.createUserWithC2(name, age);
        TestCase.assertEquals(name, response.getData().getName());
        TestCase.assertEquals(age, response.getData().getAge());
        TestCase.assertEquals("c2", response.getHeaders().get(Headers.OPEN_API_CODE.toLowerCase()));
        TestCase.assertEquals("c2", response.getHeaders().get(Headers.OPEN_API_CREDENTIAL.toLowerCase()));
        TestCase.assertTrue(response.getHeaders().containsKey(Headers.OPEN_API_REQUEST_TIME.toLowerCase()));
        TestCase.assertTrue(response.getHeaders().containsKey(Headers.OPEN_API_REQUEST_TIME_SIGNATURE.toLowerCase()));

    }

    /**
     * 创建hold on 信号量
     * @return
     */
    protected Semaphore createHoldOnSemaphore(ApplicationContext context) {
        Semaphore semaphore = new Semaphore(0);
        DiscoveryController discoveryController = context.getBean(DiscoveryController.class);
        // 刷新配置版本号
        discoveryController.incrementConfigVersion();
        MySpringBootConfigLoader.setCallBack(semaphore::release);
        return semaphore;
    }
}
