package rabbit.discovery.api.test.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rabbit.discovery.api.config.anno.FlexibleBean;
import rabbit.discovery.api.config.anno.FlexibleValue;

@Component
public class People {

    @Value("${people.name}")
    private String name;

    @Value("${people.gender}")
    private String gender;

    @FlexibleValue("${people.age}")
    private int age;

    /**
     * 简单对象注入
     */
    @FlexibleBean(propertyPrefix = "people.company")
    private Company companyObj;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Company getCompanyObj() {
        return companyObj;
    }

    public void setCompanyObj(Company companyObj) {
        this.companyObj = companyObj;
    }

    public String getGender() {
        return gender;
    }
}
