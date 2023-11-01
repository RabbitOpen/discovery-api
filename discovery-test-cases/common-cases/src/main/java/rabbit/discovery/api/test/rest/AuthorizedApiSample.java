package rabbit.discovery.api.test.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rabbit.discovery.api.common.http.anno.Body;
import rabbit.discovery.api.rest.anno.Group;
import rabbit.discovery.api.rest.anno.RestClient;
import rabbit.discovery.api.rest.http.HttpResponse;
import rabbit.discovery.api.test.bean.User;

@RestClient(application = "restApiSampleServer", contextPath = "/black")
public interface AuthorizedApiSample {

    /**
     * 未授权的接口
     * @return
     */
    @GetMapping("/unAuthorized")
    User callUnAuthorized();

    /**
     * 授权的接口
     * @return
     */
    @GetMapping("/authorized")
    User callAuthorized();
}
