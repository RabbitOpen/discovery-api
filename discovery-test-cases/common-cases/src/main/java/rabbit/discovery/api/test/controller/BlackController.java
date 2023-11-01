package rabbit.discovery.api.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rabbit.discovery.api.test.bean.User;

/**
 * 没有权限访问的接口
 */
@RestController
@RequestMapping("/forbidden")
public class BlackController {

    @GetMapping("/getUser")
    public User getUser() {
        return new User();
    }
}
