package rabbit.discovery.api.common.exception;

public class TooManyWildcardException extends DiscoveryException {

    public TooManyWildcardException(String path) {
        super("多个通配符异常: ".concat(path));
    }
}
