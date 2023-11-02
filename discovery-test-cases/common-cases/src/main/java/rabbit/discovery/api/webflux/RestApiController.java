package rabbit.discovery.api.webflux;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import rabbit.discovery.api.test.bean.User;

@RestController
@RequestMapping("/rest")
public class RestApiController {

    @PostMapping("/get/{name}/{age}")
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
}
