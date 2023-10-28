package rabbit.discovery.api.rest.facotry;

import org.springframework.beans.factory.annotation.Autowired;
import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.rest.ClientFactory;
import rabbit.discovery.api.rest.HttpRequestExecutor;
import rabbit.discovery.api.rest.http.HttpRequest;
import rabbit.discovery.api.rest.http.OpenClientExecutor;

import java.lang.reflect.Proxy;
import java.security.PrivateKey;
import java.util.function.Function;

public class OpenClientFactory extends ClientFactory {

    // 凭据
    private String credential;

    // 接口编码
    private String apiCode;

    private String baseUri;

    // 私钥字符串
    private String privateKey;

    // privateKey转换成的私钥
    private PrivateKey rsaPrivateKey;

    private ServerNode serverNode;

    @Autowired
    private OpenClientExecutor openClientExecutor;

    public OpenClientFactory(Class<?> type) {
        super(type, null);
    }

    public OpenClientFactory(String baseUri, Class<?> clzType, Function<String, String> propertyReader) {
        this(clzType);
        super.propertyReader = propertyReader;
        cacheHttpRequests();
    }

    @Override
    protected HttpRequest createHttpRequest() {
        return new HttpRequest(this);
    }

    @Override
    protected HttpRequest cloneRequest(HttpRequest request) {
        return null;
    }

    @Override
    protected HttpRequestExecutor getRequestExecutor() {
        return openClientExecutor;
    }

    @Override
    public Object getObject() throws Exception {
        OpenClientFactory factory = new OpenClientFactory(baseUri, getObjectType(), propertyReader);
        // 默认保存全局凭据
        factory.setCredential(getGlobalCredential());
        return Proxy.newProxyInstance(OpenClientFactory.class.getClassLoader(), new Class[]{getObjectType()}, factory);
    }

    /**
     * 获取全局凭据(配置在接口注解上的)
     * @return
     */
    private String getGlobalCredential() {
        return readPropertyFromConfig(this.credential);
    }

    // ------ spring 构建bean时调用set方法设置属性 --------
    public void setCredential(String credential) {
        this.credential = credential;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

}
