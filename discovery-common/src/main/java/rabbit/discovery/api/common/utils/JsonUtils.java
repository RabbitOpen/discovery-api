package rabbit.discovery.api.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import rabbit.discovery.api.common.exception.DiscoveryException;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class JsonUtils {

    private ObjectMapper mapper = new ObjectMapper();

    private static final JsonUtils inst = new JsonUtils();

    private JsonUtils() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T readValue(String json, Type type) {
        try {
            return inst.getMapper().readValue(json, getTypeFactory().constructType(type));
        } catch (JsonProcessingException e) {
            throw new DiscoveryException(e);
        }
    }

    public static <T> T readValue(String json, JavaType type) {
        try {
            return inst.getMapper().readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new DiscoveryException(e);
        }
    }

    public static String writeObject(Object data) {
        try {
            return inst.getMapper().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new DiscoveryException(e);
        }
    }

    public static JavaType constructListType(Class<? extends Collection> collectionClz, Class<?> elementType) {
        return getTypeFactory().constructCollectionType(collectionClz, elementType);
    }

    public static TypeFactory getTypeFactory() {
        return inst.getMapper().getTypeFactory();
    }

    public static <T extends Map> JavaType constructMapType(Class<T> mapType, Class<?> keyType, Class<?> valueType) {
        return getTypeFactory().constructMapType(mapType, keyType, valueType);
    }

    private ObjectMapper getMapper() {
        return mapper;
    }
}
