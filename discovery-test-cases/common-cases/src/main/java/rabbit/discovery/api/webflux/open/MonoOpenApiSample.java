package rabbit.discovery.api.webflux.open;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import rabbit.discovery.api.rest.anno.OpenApiClient;
import rabbit.discovery.api.rest.anno.OpenApiCode;
import rabbit.discovery.api.rest.anno.Retry;
import rabbit.discovery.api.test.bean.RetryData;
import reactor.core.publisher.Mono;

@OpenApiClient(credential = "c1", baseUri = "${base.url.name}", privateKey = "${open.rsa.privateKey}")
public interface MonoOpenApiSample {

    @Retry(3)
    @OpenApiCode("c1")
    @PostMapping("/open/retry/{time}")
    Mono<RetryData> monoOpenRetry(@PathVariable("time") int time);

}
