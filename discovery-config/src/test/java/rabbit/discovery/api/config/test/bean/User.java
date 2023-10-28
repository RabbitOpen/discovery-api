package rabbit.discovery.api.config.test.bean;

import rabbit.discovery.api.config.anno.FlexibleValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {

    private String name;

    @FlexibleValue(json = true, value = "${config.json}")
    private List<Car> jsonCars;

    // 不支持复杂集合对象注入，使用  @FlexibleValue替代
    private List<Car> cars;

    // 不支持复杂集合对象注入，使用  @FlexibleValue替代
    private ArrayList<Car> carList;

    // 支持简单集合对象注入
    private List<String> names;

    // 不支持数组
    private Car[] carArray;

    // 支持对象
    private Car car;

    // 支持map
    @FlexibleValue(json = true, value = "${config.map}")
    Map<String, String> map;

    public String getName() {
        return name;
    }

    public List<Car> getJsonCars() {
        return jsonCars;
    }

    public List<Car> getCars() {
        return cars;
    }

    public ArrayList<Car> getCarList() {
        return carList;
    }

    public List<String> getNames() {
        return names;
    }

    public Car[] getCarArray() {
        return carArray;
    }

    public Car getCar() {
        return car;
    }

    public Map<String, String> getMap() {
        return map;
    }
}
