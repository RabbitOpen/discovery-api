package rabbit.discovery.api.config.test;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.config.context.FlexibleValueMeta;
import rabbit.discovery.api.config.context.InjectType;
import rabbit.discovery.api.config.exception.InvalidFormatException;
import rabbit.discovery.api.config.test.bean.User;

import java.util.Properties;

@RunWith(JUnit4.class)
public class FlexibleBeanTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void flexibleBeanTest() {
        Properties properties = getProperties();
        User user = new User();
        FlexibleValueMeta meta = new FlexibleValueMeta(user, (bean, field, oldValue, newValue)
                -> logger.info("value changed: {}", field.getName()));
        // 解析
        meta.resolvePropertyMeta("config");
        TestCase.assertEquals(3, meta.getPropertyNameMap().size());
        TestCase.assertTrue(meta.getPropertyNameMap().containsKey("config.name"));
        FlexibleValueMeta carMeta = meta.getChildBeanMeta().get("car");
        TestCase.assertTrue(carMeta.getPropertyNameMap().containsKey("config.car.id"));

        // 注入值
        meta.inject(s -> properties.containsKey(s) ? properties.get(s).toString() : null);
        TestCase.assertEquals(0, user.getNames().size());
        properties.put("config.names[0]", "zhang3");
        properties.put("config.names[1]", "zhang4");
        meta.inject(s -> properties.containsKey(s) ? properties.get(s).toString() : null, InjectType.UPDATE);
        TestCase.assertEquals(2, user.getNames().size());
        TestCase.assertEquals("zhang3", user.getNames().get(0));
        TestCase.assertEquals("zhang4", user.getNames().get(1));

        TestCase.assertEquals(2, user.getJsonCars().size());
        TestCase.assertEquals("1", user.getJsonCars().get(0).getId());
        TestCase.assertEquals("2", user.getJsonCars().get(1).getId());

        TestCase.assertEquals("zhang3", user.getName());

        TestCase.assertEquals("川A000000", user.getCar().getId());
        TestCase.assertEquals("bmw", user.getCar().getType());
        TestCase.assertEquals(2, user.getCar().getOwner().size());
        TestCase.assertTrue(user.getCar().getOwner().contains("bmw"));
        TestCase.assertTrue(user.getCar().getOwner().contains("benz"));

        // 支持map
        TestCase.assertEquals(1, user.getMap().size());

        // 不支数组
        TestCase.assertNull(user.getCarArray());
        // 不支持复杂集合对象
        TestCase.assertEquals(0, user.getCars().size());
        TestCase.assertEquals(0, user.getCarList().size());

    }

    @Test
    public void readNameTest() {
        FlexibleValueMeta meta = new FlexibleValueMeta(null);
        TestCase.assertEquals("abc", meta.readConfigName("${abc}"));
        TestCase.assertEquals("2", meta.readDefaultValue("${abc:2}"));
        TestCase.assertEquals("2", meta.readDefaultValue("${abc:2:3}"));
        TestCase.assertNull(meta.readDefaultValue("${abc:}"));
        try {
            meta.readConfigName("${abc}}");
            throw new RuntimeException();
        } catch (InvalidFormatException e) {

        }
        try {
            meta.readConfigName("${abc");
            throw new RuntimeException();
        } catch (InvalidFormatException e) {

        }

        try {
            meta.readConfigName("${abc}2");
            throw new RuntimeException();
        } catch (InvalidFormatException e) {

        }
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.put("config.name", "zhang3");

        // jsonCars
        properties.put("config.json", "[{\"id\":\"1\"}, {\"id\":\"2\"}]");

        // map
        properties.put("config.map", "{\"id\":\"1\"}");

        //car
        properties.put("config.car.id", "川A000000");
        properties.put("config.car.type", "bmw");
        properties.put("config.car.owner[0]", "bmw");
        properties.put("config.car.owner[1]", "benz");

        // cars
        properties.put("config.cars[0].id", "川A000000");
        properties.put("config.cars[0].type", "川A000000");

        // carList
        properties.put("config.carList[0].id", "川A000000");
        properties.put("config.carList[0].type", "川A000000");
        return properties;
    }
}
