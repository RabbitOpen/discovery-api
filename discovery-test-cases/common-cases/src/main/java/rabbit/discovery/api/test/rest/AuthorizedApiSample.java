package rabbit.discovery.api.test.rest;

import org.springframework.web.bind.annotation.GetMapping;
import rabbit.discovery.api.rest.anno.RestClient;
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
