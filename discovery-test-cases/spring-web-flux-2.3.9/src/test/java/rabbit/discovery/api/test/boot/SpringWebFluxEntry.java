package rabbit.discovery.api.test.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import rabbit.discovery.api.rest.EnableOpenClients;
import rabbit.discovery.api.rest.EnableRestClients;

@SpringBootApplication(scanBasePackages = {"rabbit.discovery.api.test", "rabbit.discovery.api.webflux"})
@EnableOpenClients(basePackages = {"rabbit.discovery.api.test.open"})
@EnableRestClients(basePackages = {"rabbit.discovery.api.test.rest", "rabbit.discovery.api.webflux.rest"})
@EnableFeignClients
public class SpringWebFluxEntry {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebFluxEntry.class);
    }
}
