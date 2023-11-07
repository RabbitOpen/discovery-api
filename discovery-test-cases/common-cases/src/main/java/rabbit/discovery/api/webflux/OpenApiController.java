package rabbit.discovery.api.webflux;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rabbit.discovery.api.test.bean.RetryData;
import rabbit.discovery.api.test.bean.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * open api 演示
 */
@RestController
@RequestMapping("/open")
public class OpenApiController {

    private Map<Integer, AtomicInteger> map = new HashMap<>();

    @PostMapping("/get/{name}/{age}")
    public User getUser(@PathVariable("name") String name, @PathVariable("age") int age,
                        ServerHttpRequest request, ServerHttpResponse response) {
        HttpHeaders names = request.getHeaders();
        names.forEach((n, v) -> {
            if ("Content-length".equalsIgnoreCase(n)) {
                return;
            }
            response.getHeaders().set(n, v.get(0));
        });
        return new User(name, age);
    }

    @PostMapping("/retry/{time}")
    public RetryData retry(@PathVariable("time") int time, ServerHttpResponse response) {
        map.computeIfAbsent(time, k->new AtomicInteger(0)).incrementAndGet();
        if (time < 10) {
            response.setRawStatusCode(500);
        }
        // 返回重试的次数
        return new RetryData((map.get(time).get()));
    }
}
