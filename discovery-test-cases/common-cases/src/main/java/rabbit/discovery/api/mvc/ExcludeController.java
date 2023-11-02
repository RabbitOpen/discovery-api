package rabbit.discovery.api.mvc;

import org.springframework.web.bind.annotation.*;
import rabbit.discovery.api.rest.Policy;
import rabbit.discovery.api.rest.anno.Declaration;

/**
 * 接口上报，排除策略
 */
@RestController
@RequestMapping("/exclude")
@Declaration(methods = {"getUser", "postUser", "requestUser", "deleteUser", "putUser", "patchUser"}, policy = Policy.EXCLUDE)
public class ExcludeController {

    @GetMapping("/getUser")
    public void getUser() {

    }

    @PostMapping(name = "/postUser")
    public void postUser() {

    }

    @RequestMapping(path = "/requestUser")
    public void requestUser() {

    }

    @DeleteMapping(path = "/deleteUser")
    public void deleteUser() {

    }

    @PutMapping("/putUser")
    public void putUser() {

    }

    @PatchMapping("/patchUser")
    public void patchUser() {

    }

    @RequestMapping(path = {"/exclude1", "/exclude2"})
    public void exclude() {

    }
}
