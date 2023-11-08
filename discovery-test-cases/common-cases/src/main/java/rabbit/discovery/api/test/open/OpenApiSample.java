package rabbit.discovery.api.test.open;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.rest.anno.Credential;
import rabbit.discovery.api.rest.anno.OpenApiClient;
import rabbit.discovery.api.rest.anno.OpenApiCode;
import rabbit.discovery.api.rest.anno.TargetServer;
import rabbit.discovery.api.rest.http.HttpResponse;
import rabbit.discovery.api.test.bean.User;

@OpenApiClient(credential = "c1", baseUri = "${base.url.name}", privateKey = "${open.rsa.privateKey}")
public interface OpenApiSample {

    @OpenApiCode("c1")
    @PostMapping("/open/get/{name}/{age}")
    HttpResponse<User> getUser(@PathVariable("name") String name, @PathVariable("age") int age);

    /**
     * 直接指定server
     * @param name
     * @param age
     * @param serverNode
     * @return
     */
    @OpenApiCode("c9")
    @Credential("c3")
    @PostMapping("/open/get/{name}/{age}")
    HttpResponse<User> getUser(@PathVariable("name") String name, @PathVariable("age") int age,
                               @TargetServer ServerNode serverNode);

    /**
     * 用凭据2调用
     * @param name
     * @param age
     * @return
     */
    @OpenApiCode("c2")
    @Credential("c2")
    @PostMapping("/open/get/{name}/{age}")
    HttpResponse<User> getUserWithC2(@PathVariable("name") String name, @PathVariable("age") int age);
}
