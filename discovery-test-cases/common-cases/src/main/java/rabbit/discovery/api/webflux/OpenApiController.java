package rabbit.discovery.api.webflux;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rabbit.discovery.api.test.bean.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * open api 演示
 */
@RestController
@RequestMapping("/open")
public class OpenApiController {

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
}
