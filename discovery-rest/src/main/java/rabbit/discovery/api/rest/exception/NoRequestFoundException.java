package rabbit.discovery.api.rest.exception;

import rabbit.discovery.api.common.exception.RestApiException;

import java.lang.reflect.Method;

public class NoRequestFoundException extends RestApiException {

    public NoRequestFoundException(Method method) {
        super("no request definition found on method[" + method + "]");
    }
}
