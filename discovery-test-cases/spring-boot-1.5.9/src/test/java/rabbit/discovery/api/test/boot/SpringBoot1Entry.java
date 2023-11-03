package rabbit.discovery.api.test.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import rabbit.discovery.api.rest.EnableOpenClients;
import rabbit.discovery.api.rest.EnableRestClients;

@SpringBootApplication(scanBasePackages = {"rabbit.discovery.api.test", "rabbit.discovery.api.mvc"})
@EnableOpenClients(basePackages = {"rabbit.discovery.api.test.open"})
@EnableRestClients(basePackages = {"rabbit.discovery.api.test.rest"})
@EnableFeignClients
public class SpringBoot1Entry {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot1Entry.class);
    }
}
