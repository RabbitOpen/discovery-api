package rabbit.discovery.api.mvc;

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
                           HttpServletRequest request, HttpServletResponse response) {
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String n = names.nextElement();
            if ("Content-length".equalsIgnoreCase(n)) {
                continue;
            }
            response.setHeader(n.toLowerCase(), request.getHeader(n));
        }
        return new User(name, age);
    }
}
