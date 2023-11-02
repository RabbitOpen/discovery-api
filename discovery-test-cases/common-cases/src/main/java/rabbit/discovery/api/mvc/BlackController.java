package rabbit.discovery.api.mvc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rabbit.discovery.api.test.bean.User;

/**
 * 需要授权才能访问的接口
 */
@RestController
@RequestMapping("/black")
public class BlackController {

    @GetMapping("/unAuthorized")
    public User unAuthorized() {
        return new User();
    }

    @GetMapping("/authorized")
    public User callAuthorized() {
        return new User("authorized", 100);
    }
}
