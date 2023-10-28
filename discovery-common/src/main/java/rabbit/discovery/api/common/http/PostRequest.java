package rabbit.discovery.api.common.http;

import rabbit.discovery.api.common.enums.HttpMethod;

import static rabbit.discovery.api.common.enums.HttpMethod.POST;

public class PostRequest extends Request {

    public PostRequest(String url) {
        super(url);
    }

    @Override
    protected HttpMethod getMethod() {
        return POST;
    }
}
