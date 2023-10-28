package rabbit.discovery.api.config.exception;

import rabbit.discovery.api.common.exception.ConfigException;

import java.lang.reflect.Type;

public class UnSupportedTypeException extends ConfigException {

    public UnSupportedTypeException(Type type) {
        super("不支持的注入类型[".concat(type.getTypeName()).concat("]"));
    }
}
