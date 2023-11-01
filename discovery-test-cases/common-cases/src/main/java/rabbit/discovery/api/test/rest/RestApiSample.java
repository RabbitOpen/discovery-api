package rabbit.discovery.api.test.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rabbit.discovery.api.common.http.anno.Body;
import rabbit.discovery.api.rest.anno.RestClient;
import rabbit.discovery.api.rest.http.HttpResponse;
import rabbit.discovery.api.test.bean.User;

@RestClient(application = "restApiSampleServer", contextPath = "/rest")
public interface RestApiSample {

    @PostMapping("/get/{name}/{age}")
    User getUser(@PathVariable("name") String name, @PathVariable("age") int age, @Body User user);

    @PostMapping("/get/{name}/{age}")
    HttpResponse<User> getUserAndHeaders(@PathVariable("name") String name, @PathVariable("age") int age,
                                         @RequestBody User user);

}
