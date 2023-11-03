package rabbit.discovery.api.test.boot;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rabbit.discovery.api.test.bean.User;

@FeignClient(name="restApiSampleServer", path = "rest")
public interface FeignServiceClient {

    @PostMapping("/get/{name}/{age}")
    User getUser(@PathVariable("name") String name, @PathVariable("age") int age, @RequestBody User user);
}
