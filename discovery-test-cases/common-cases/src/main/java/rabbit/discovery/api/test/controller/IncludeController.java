package rabbit.discovery.api.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rabbit.discovery.api.rest.Policy;
import rabbit.discovery.api.rest.anno.Declaration;

/**
 * 接口上报，包含策略
 */
@Controller
@RequestMapping("/include")
@Declaration(methods = {"getUser", "postUser", "requestUser", "deleteUser", "putUser", "patchUser"}, policy = Policy.INCLUDE)
public class IncludeController {

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

    @RequestMapping(path = "/exclude")
    public void exclude() {

    }
}
