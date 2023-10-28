package rabbit.discovery.api.rest.exception;

import java.lang.reflect.Method;

public class InvalidRequestException extends NoRequestFoundException {

    public InvalidRequestException(Method method) {
        super(method);
    }
}
