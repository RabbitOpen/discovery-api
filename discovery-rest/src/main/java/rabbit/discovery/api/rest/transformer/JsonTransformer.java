package rabbit.discovery.api.rest.transformer;

import rabbit.discovery.api.common.utils.JsonUtils;
import rabbit.discovery.api.rest.HttpTransformer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * json transformer
 */
public class JsonTransformer implements HttpTransformer {

    @Override
    public String transformRequest(Method method, Object requestBody) {
        return JsonUtils.writeObject(requestBody);
    }

    @Override
    public <T> T transformResponse(Method method, Type resultType, Map<String, String> responseHeaders, String responseBody) {
        return JsonUtils.readValue(responseBody, resultType);
    }
}
