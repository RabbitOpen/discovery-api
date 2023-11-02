package rabbit.discovery.api.test;

import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import rabbit.discovery.api.common.Headers;
import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.common.exception.RestApiException;
import rabbit.discovery.api.common.rpc.ApiDescription;
import rabbit.discovery.api.rest.http.HttpResponse;
import rabbit.discovery.api.test.bean.People;
import rabbit.discovery.api.test.bean.User;
import rabbit.discovery.api.mvc.ConfigController;
import rabbit.discovery.api.mvc.DiscoveryController;
import rabbit.discovery.api.mvc.ExcludeController;
import rabbit.discovery.api.mvc.IncludeController;
import rabbit.discovery.api.test.open.OpenApiSample;
import rabbit.discovery.api.test.rest.AuthorizedApiSample;
import rabbit.discovery.api.test.rest.RestApiSample;
import rabbit.discovery.api.test.spi.MySpringBootConfigLoader;
import rabbit.discovery.api.test.spi.TestApiReportService;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 核心用例
 */
public class CoreCases {


    /**
     * 加载远程配置case
     *
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
     * 接口上报验证
     */
    public void reportServiceCase() {
        TestCase.assertEquals(2, TestApiReportService.getMap().size());
        List<ApiDescription> exclude = TestApiReportService.getMap().get(ExcludeController.class.getName());
        TestCase.assertEquals(2, exclude.size());
        TestCase.assertEquals(ExcludeController.class.getName().concat(".exclude"), exclude.get(0).getName());
        TestCase.assertEquals("/exclude/exclude1", exclude.get(0).getPath());
        TestCase.assertEquals(ExcludeController.class.getName().concat(".exclude"), exclude.get(1).getName());
        TestCase.assertEquals("/exclude/exclude2", exclude.get(1).getPath());
        TestCase.assertEquals(HttpMethod.GET, exclude.get(0).getMethod());

        List<ApiDescription> include = TestApiReportService.getMap().get(IncludeController.class.getName());
        TestCase.assertEquals(6, include.size());
        include.forEach(a -> {
            if (a.getName().equals("exclude")) {
                throw new RestApiException("不可能啊");
            }
        });
    }

    /**
     * open api调用示例
     *
     * @param context
     */
    public void openApiCase(ApplicationContext context) {
        OpenApiSample apiSample = context.getBean(OpenApiSample.class);
        String name = "zhang3";
        int age = 12;
        HttpResponse<User> response = apiSample.getUser(name, age);
        TestCase.assertEquals(name, response.getData().getName());
        TestCase.assertEquals(age, response.getData().getAge());
        TestCase.assertEquals("c1", response.getHeaders().get(Headers.OPEN_API_CODE.toLowerCase()));
        TestCase.assertEquals("c1", response.getHeaders().get(Headers.OPEN_API_CREDENTIAL.toLowerCase()));
        TestCase.assertTrue(response.getHeaders().containsKey(Headers.OPEN_API_REQUEST_TIME.toLowerCase()));
        TestCase.assertTrue(response.getHeaders().containsKey(Headers.OPEN_API_REQUEST_TIME_SIGNATURE.toLowerCase()));
        response = apiSample.getUserWithC2(name, age);
        TestCase.assertEquals(name, response.getData().getName());
        TestCase.assertEquals(age, response.getData().getAge());
        TestCase.assertEquals("c2", response.getHeaders().get(Headers.OPEN_API_CODE.toLowerCase()));
        TestCase.assertEquals("c2", response.getHeaders().get(Headers.OPEN_API_CREDENTIAL.toLowerCase()));
        TestCase.assertTrue(response.getHeaders().containsKey(Headers.OPEN_API_REQUEST_TIME.toLowerCase()));
        TestCase.assertTrue(response.getHeaders().containsKey(Headers.OPEN_API_REQUEST_TIME_SIGNATURE.toLowerCase()));
    }

    /**
     * rest api 用例
     *
     * @param context
     */
    public void restApiCase(ApplicationContext context) {
        RestApiSample apiSample = context.getBean(RestApiSample.class);
        String name = "zhang3";
        int age = 12;
        User user = apiSample.getUser(name, 123, new User(name, age));
        TestCase.assertEquals(name, user.getName());
        TestCase.assertEquals(123, user.getAge());
        // 调用local分组
        user = apiSample.getUser(name, 123, new User(name, age), "local");
        TestCase.assertEquals(name, user.getName());
        TestCase.assertEquals(123, user.getAge());
        HttpResponse<User> response = apiSample.getUserAndHeaders(name, 123, new User(name, age));
        TestCase.assertEquals(name, response.getData().getName());
        TestCase.assertEquals(123, response.getData().getAge());
        TestCase.assertTrue(response.getHeaders().containsKey(Headers.API_VERSION.toLowerCase()));
        TestCase.assertTrue(response.getHeaders().containsKey(Headers.APPLICATION_CODE.toLowerCase()));
        TestCase.assertTrue(response.getHeaders().containsKey(Headers.REQUEST_TIME.toLowerCase()));
        TestCase.assertTrue(response.getHeaders().containsKey(Headers.REQUEST_TIME_SIGNATURE.toLowerCase()));
    }

    /**
     * 授权接口访问用例
     */
    public void authorizationUrlCase(ApplicationContext context) {
        AuthorizedApiSample apiSample = context.getBean(AuthorizedApiSample.class);
        try {
            apiSample.callUnAuthorized();
            throw new RuntimeException();
        } catch (RestApiException e) {
            TestCase.assertTrue(e.getMessage().contains("unAuthorized api"));
        }
        User user = apiSample.callAuthorized();
        TestCase.assertEquals(100, user.getAge());
        TestCase.assertEquals("authorized", user.getName());
    }

    /**
     * 创建hold on 信号量
     *
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
