package rabbit.discovery.api.test.open;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import rabbit.discovery.api.rest.anno.Credential;
import rabbit.discovery.api.rest.anno.OpenApiClient;
import rabbit.discovery.api.rest.anno.OpenApiCode;
import rabbit.discovery.api.rest.http.HttpResponse;
import rabbit.discovery.api.test.bean.User;

@OpenApiClient(credential = "c1", baseUri = "${base.url.name}", privateKey = "${open.rsa.privateKey}")
public interface OpenApiSample {

    @OpenApiCode("c1")
    @PostMapping("/open/create/{name}/{age}")
    HttpResponse<User> createUser(@PathVariable("name") String name, @PathVariable("age") int age);

    /**
     * 用凭据2调用
     * @param name
     * @param age
     * @return
     */
    @OpenApiCode("c2")
    @Credential("c2")
    @PostMapping("/open/create/{name}/{age}")
    HttpResponse<User> createUserWithC2(@PathVariable("name") String name, @PathVariable("age") int age);
}
