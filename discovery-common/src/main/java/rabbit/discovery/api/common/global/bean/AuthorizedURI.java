package rabbit.discovery.api.common.global.bean;

import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.common.utils.PathPattern;

public class AuthorizedURI {

    private PathPattern pattern;

    private HttpMethod method;

    public AuthorizedURI(PathPattern pattern, HttpMethod method) {
        this.pattern = pattern;
        this.method = method;
    }

    public PathPattern getPattern() {
        return pattern;
    }

    public HttpMethod getMethod() {
        return method;
    }
}
