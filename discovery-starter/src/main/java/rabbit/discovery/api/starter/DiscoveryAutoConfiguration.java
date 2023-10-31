package rabbit.discovery.api.starter;

import org.springframework.context.annotation.Import;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.TraceConfiguration;
import rabbit.discovery.api.config.SpringBootConfigProcessor;
import rabbit.discovery.api.rest.report.ApiCollector;

/**
 * 该类通过spring.factories启动，必须是class
 */
@org.springframework.context.annotation.Configuration
@Import({
        Configuration.class,
        TraceConfiguration.class,
        SpringBootStarter.class,
        SpringBootConfigProcessor.class,    // 为 @FlexibleValue属性注入值
        ApiCollector.class,
})
public class DiscoveryAutoConfiguration {
}
