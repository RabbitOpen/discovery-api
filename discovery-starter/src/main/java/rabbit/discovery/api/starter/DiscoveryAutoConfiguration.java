package rabbit.discovery.api.starter;

import org.springframework.context.annotation.Import;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.TraceConfiguration;
import rabbit.discovery.api.rest.report.ApiCollector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({
        Configuration.class,
        TraceConfiguration.class,
        SpringBootStarter.class,
        ApiCollector.class,
})
public @interface DiscoveryAutoConfiguration {
}
