package rabbit.discovery.api.config.exception;

import rabbit.discovery.api.common.exception.ConfigException;

public class InvalidFormatException extends ConfigException {

    public InvalidFormatException(String content) {
        super(String.format("annotation[%s] pattern is invalid, ${name:value} for example", content));
    }
}
