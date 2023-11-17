package rabbit.discovery.api.webflux.rest;

import org.springframework.web.bind.annotation.*;
import rabbit.discovery.api.common.http.anno.Body;
import rabbit.discovery.api.rest.anno.Cluster;
import rabbit.discovery.api.rest.anno.RestClient;
import rabbit.discovery.api.rest.anno.Retry;
import rabbit.discovery.api.rest.http.HttpResponse;
import rabbit.discovery.api.test.bean.RetryData;
import rabbit.discovery.api.test.bean.User;
import reactor.core.publisher.Mono;

@RestClient(application = "restApiSampleServer", contextPath = "/rest")
public interface MonoRestApiSample {

    @RequestMapping("/get/{name}/{age}")
    Mono<User> getUser(@PathVariable("name") String name, @PathVariable("age") int age, @Body User user);

    @GetMapping("/get/{name}/{age}")
    Mono<HttpResponse<User>> getUserAndHeaders(@PathVariable("name") String name, @PathVariable("age") int age,
                                               @RequestBody User user);

    /**
     * 调用指定cluster的服务
     *
     * @param name
     * @param age
     * @param user
     * @param cluster
     * @return
     */
    @GetMapping("/get/{name}/{age}")
    Mono<User> getUser(@PathVariable("name") String name, @PathVariable("age") int age,
                       @RequestBody User user, @Cluster String cluster);

    @Retry(3)
    @PostMapping("/retry/{time}")
    Mono<RetryData> retry(@PathVariable("time") int time);

    @GetMapping("/hello")
    Mono<String> hello();

    /**
     * void http
     */
    @GetMapping("/void")
    void callVoid();

    /**
     * void http
     */
    @GetMapping("/void")
    Mono<Void> callMonoVoid();

    /**
     * void http
     */
    @GetMapping("/void")
    Mono<HttpResponse<Void>> callMonoVoidWithHeaders();
}
