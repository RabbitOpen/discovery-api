package rabbit.discovery.api.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import rabbit.discovery.api.common.exception.DiscoveryException;

import java.lang.reflect.Type;
import java.util.Collection;

public class JsonUtils {

    private ObjectMapper mapper = new ObjectMapper();

    private static final JsonUtils UTILS = new JsonUtils();

    private JsonUtils() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T readValue(String json, Type type) {
        try {
            return UTILS.getMapper().readValue(json, UTILS.getMapper().getTypeFactory().constructType(type));
        } catch (JsonProcessingException e) {
            throw new DiscoveryException(e);
        }
    }

    public static <T> T readValue(String json, JavaType type) {
        try {
            return UTILS.getMapper().readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new DiscoveryException(e);
        }
    }

    public static String writeObject(Object data) {
        try {
            return UTILS.getMapper().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new DiscoveryException(e);
        }
    }

    public static JavaType constructListType(Class<? extends Collection> collectionClz, Class<?> elementType) {
        return UTILS.getMapper().getTypeFactory().constructCollectionType(collectionClz, elementType);
    }

    private ObjectMapper getMapper() {
        return mapper;
    }
}
