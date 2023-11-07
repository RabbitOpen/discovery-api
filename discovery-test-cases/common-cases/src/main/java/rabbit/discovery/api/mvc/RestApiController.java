package rabbit.discovery.api.mvc;

import org.springframework.web.bind.annotation.*;
import rabbit.discovery.api.test.bean.RetryData;
import rabbit.discovery.api.test.bean.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/rest")
public class RestApiController {

    private Map<Integer, AtomicInteger> map = new HashMap<>();

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

    @PostMapping("/retry/{time}")
    public RetryData retry(@PathVariable("time") int time, HttpServletResponse response) {
        map.computeIfAbsent(time, k->new AtomicInteger(0)).incrementAndGet();
        if (time < 10) {
            response.setStatus(500);
        }
        // 返回重试的次数
        return new RetryData((map.get(time).get()));
    }
}
