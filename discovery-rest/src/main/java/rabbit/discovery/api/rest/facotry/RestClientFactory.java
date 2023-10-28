package rabbit.discovery.api.rest.facotry;

import rabbit.discovery.api.rest.ClientFactory;
import rabbit.discovery.api.rest.HttpRequestExecutor;
import rabbit.discovery.api.rest.http.HttpRequest;

public class RestClientFactory extends ClientFactory {

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
}
