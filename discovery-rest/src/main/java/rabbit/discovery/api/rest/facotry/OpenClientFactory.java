package rabbit.discovery.api.rest.facotry;

import org.springframework.beans.factory.FactoryBean;
import rabbit.discovery.api.rest.ClientFactory;
import rabbit.discovery.api.rest.HttpRequestExecutor;
import rabbit.discovery.api.rest.http.HttpRequest;

public class OpenClientFactory extends ClientFactory {

    @Override
    protected HttpRequest createHttpRequest() {
        return null;
    }

    @Override
    protected HttpRequest cloneRequest(HttpRequest request) {
        return null;
    }

    @Override
    protected HttpRequestExecutor getRequestExecutor() {
        return null;
    }

    @Override
    public Object getObject() throws Exception {
        return null;
    }
}
