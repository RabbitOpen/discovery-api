package rabbit.discovery.api.rest.facotry;

import org.springframework.beans.factory.annotation.Autowired;
import rabbit.discovery.api.common.Headers;
import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.common.exception.RestApiException;
import rabbit.discovery.api.common.utils.RsaUtils;
import rabbit.discovery.api.rest.ClientFactory;
import rabbit.discovery.api.rest.HttpRequestExecutor;
import rabbit.discovery.api.rest.anno.Credential;
import rabbit.discovery.api.rest.anno.OpenApiCode;
import rabbit.discovery.api.rest.http.HttpRequest;
import rabbit.discovery.api.rest.http.OpenClientExecutor;
import rabbit.flt.common.utils.StringUtils;

import java.lang.reflect.Proxy;
import java.security.PrivateKey;
import java.util.function.Function;

import static rabbit.discovery.api.common.utils.HexUtils.toHex;
import static rabbit.discovery.api.common.utils.RsaUtils.signWithPrivateKey;

public class OpenClientFactory extends ClientFactory {

    // 凭据
    private String credential;

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
        setBaseUri(baseUri);
        this.serverNode = new ServerNode(readConfigProperty(baseUri.trim()));
        cacheHttpRequests();
    }

    @Override
    protected HttpRequest createHttpRequest() {
        return new HttpRequest(this);
    }

    @Override
    protected HttpRequest cloneRequest(HttpRequest request) {
        HttpRequest httpRequest = createHttpRequest();
        httpRequest.setMethod(request.getMethod());
        httpRequest.setHttpMethod(request.getHttpMethod());
        httpRequest.setUri(request.getUri());
        httpRequest.setResultType(request.getMethod().getGenericReturnType());
        OpenApiCode apiCode = request.getMethod().getAnnotation(OpenApiCode.class);
        if (null == apiCode) {
            throw new RestApiException("api code is not defined");
        }
        httpRequest.setHeader(Headers.OPEN_API_CODE, readConfigProperty(apiCode.value().trim()));
        httpRequest.setHeader(Headers.OPEN_API_CREDENTIAL, getCredential(request));
        String requestTime = Long.toString(System.currentTimeMillis());
        httpRequest.setHeader(Headers.OPEN_API_REQUEST_TIME, requestTime);
        httpRequest.setHeader(Headers.OPEN_API_REQUEST_TIME_SIGNATURE, toHex(signWithPrivateKey(requestTime, rsaPrivateKey)));
        return httpRequest;
    }

    /**
     * 获取接口调用凭据
     *
     * @param request
     * @return
     */
    private String getCredential(HttpRequest request) {
        Credential c = request.getMethod().getAnnotation(Credential.class);
        if (null == c || StringUtils.isEmpty(c.value())) {
            return this.credential;
        }
        return readConfigProperty(c.value().trim());
    }

    @Override
    protected HttpRequestExecutor getRequestExecutor() {
        return openClientExecutor;
    }

    @Override
    public Object getObject() {
        OpenClientFactory factory = new OpenClientFactory(baseUri, getObjectType(), propertyReader);
        // 默认保存全局凭据
        factory.setCredential(readConfigProperty(this.credential));
        factory.setPrivateKey(readConfigProperty(privateKey));
        factory.setOpenClientExecutor(openClientExecutor);
        return Proxy.newProxyInstance(OpenClientFactory.class.getClassLoader(), new Class[]{getObjectType()}, factory);
    }

    public ServerNode getServerNode() {
        return serverNode;
    }

    // ------ spring 构建bean时调用set方法设置属性 --------
    public void setCredential(String credential) {
        this.credential = credential;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
        if (StringUtils.isEmpty(privateKey)) {
            throw new RestApiException("密钥不能为空");
        }
        if (null != propertyReader) {
            rsaPrivateKey = RsaUtils.loadPrivateKeyFromString(readConfigProperty(privateKey));
        }
    }

    public void setOpenClientExecutor(OpenClientExecutor openClientExecutor) {
        this.openClientExecutor = openClientExecutor;
    }

}
