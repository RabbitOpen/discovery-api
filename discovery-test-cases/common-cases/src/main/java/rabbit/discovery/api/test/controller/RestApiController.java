package rabbit.discovery.api.test.controller;

import org.springframework.web.bind.annotation.*;
import rabbit.discovery.api.test.bean.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@RestController
@RequestMapping("/rest")
public class RestApiController {

    @PostMapping("/get/{name}/{age}")
    public User getUser(@PathVariable("name") String name, @PathVariable("age") int age,
                           @RequestBody(required = false) User requestUser,
                           HttpServletRequest request, HttpServletResponse response) {
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String n = names.nextElement();
            if ("Content-length".equalsIgnoreCase(n)) {
                continue;
            }
            response.setHeader(n, request.getHeader(n).toLowerCase());
        }
        if (null == requestUser) {
            return new User(name, age);
        } else {
            requestUser.setName(name);
            requestUser.setAge(age);
            return requestUser;
        }
    }
}
