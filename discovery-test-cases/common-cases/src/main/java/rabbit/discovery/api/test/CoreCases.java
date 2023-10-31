package rabbit.discovery.api.test;

import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import rabbit.discovery.api.test.bean.People;
import rabbit.discovery.api.test.controller.ConfigController;

/**
 * 核心用例
 */
public class CoreCases {


    /**
     * 加载远程配置case
     * @param context
     */
    public void configLoadCase(ApplicationContext context) {
        People people = context.getBean(People.class);
        ConfigController controller = context.getBean(ConfigController.class);
        TestCase.assertEquals(controller.getAge(), people.getAge());
        TestCase.assertEquals(controller.getName(), people.getName());
        TestCase.assertEquals(controller.getGender(), people.getGender());
        TestCase.assertEquals(controller.getCompanyName(), people.getCompanyObj().getName());
        TestCase.assertEquals(controller.getCompanyAddress(), people.getCompanyObj().getAddress());
    }
}
