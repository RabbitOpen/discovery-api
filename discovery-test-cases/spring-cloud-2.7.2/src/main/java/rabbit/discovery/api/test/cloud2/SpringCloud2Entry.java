package rabbit.discovery.api.test.cloud2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rabbit.discovery.api.rest.EnableOpenClients;

@SpringBootApplication(scanBasePackages = {"rabbit.discovery.api.test"})
@EnableOpenClients(basePackages = {"rabbit.discovery.api.test.open"})
public class SpringCloud2Entry {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloud2Entry.class);
    }
}
