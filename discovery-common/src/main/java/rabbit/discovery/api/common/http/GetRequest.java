package rabbit.discovery.api.common.http;

import rabbit.discovery.api.common.enums.HttpMethod;

import java.util.Map;

import static rabbit.discovery.api.common.enums.HttpMethod.GET;

public class GetRequest extends Request {

    public GetRequest(String url) {
        super(url);
    }

    public GetRequest(String url, Map<String, String> headers) {
        super(url, headers);
    }

    public GetRequest(String url, Map<String, String> headers, Map<String, String> pathVariables) {
        super(url, headers, pathVariables);
    }

    @Override
    protected HttpMethod getMethod() {
        return GET;
    }
}
