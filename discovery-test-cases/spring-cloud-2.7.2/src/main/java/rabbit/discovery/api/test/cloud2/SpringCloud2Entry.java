package rabbit.discovery.api.test.cloud2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"rabbit.discovery.api.test"})
public class SpringCloud2Entry {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloud2Entry.class);
    }
}
