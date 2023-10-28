package rabbit.discovery.api.common.exception;

public class DiscoveryException extends RuntimeException {

    public DiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiscoveryException(String message) {
        super(message);
    }

    public DiscoveryException(Throwable cause) {
        super(cause);
    }
}
