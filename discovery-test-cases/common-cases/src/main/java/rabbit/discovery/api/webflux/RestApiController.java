package rabbit.discovery.api.webflux;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import rabbit.discovery.api.test.bean.RetryData;
import rabbit.discovery.api.test.bean.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/rest")
public class RestApiController {

    private Map<Integer, AtomicInteger> map = new HashMap<>();

    @RequestMapping(value = "/get/{name}/{age}", method = {RequestMethod.POST, RequestMethod.GET})
    public User getUser(@PathVariable("name") String name, @PathVariable("age") int age,
                        @RequestBody(required = false) User requestUser,
                        ServerHttpRequest request, ServerHttpResponse response) {
        HttpHeaders names = request.getHeaders();
        names.forEach((n, v) -> {
            if ("Content-length".equalsIgnoreCase(n)) {
                return;
            }
            response.getHeaders().set(n, v.get(0));
        });
        if (null == requestUser) {
            return new User(name, age);
        } else {
            requestUser.setName(name);
            requestUser.setAge(age);
            return requestUser;
        }
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
