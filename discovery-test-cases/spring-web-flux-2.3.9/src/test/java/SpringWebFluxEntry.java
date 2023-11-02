import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rabbit.discovery.api.rest.EnableOpenClients;
import rabbit.discovery.api.rest.EnableRestClients;

@SpringBootApplication(scanBasePackages = {"rabbit.discovery.api.test"})
@EnableOpenClients(basePackages = {"rabbit.discovery.api.test.open"})
@EnableRestClients(basePackages = {"rabbit.discovery.api.test.rest"})
public class SpringWebFluxEntry {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebFluxEntry.class);
    }
}
