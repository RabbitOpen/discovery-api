package rabbit.discovery.api.common.exception;

public class RestApiException extends RuntimeException {

    public RestApiException() {
    }

    public RestApiException(String message) {
        super(message);
    }

    public RestApiException(Throwable cause) {
        super(cause);
    }
}
