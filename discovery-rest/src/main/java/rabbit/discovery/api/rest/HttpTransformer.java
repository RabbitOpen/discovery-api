package rabbit.discovery.api.rest;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * http 报文转换器
 * 用户可以自行定义自己的报文转换器，通过spring bean注入
 */
public interface HttpTransformer {

    /**
     * 转换request body
     *
     * @param method      默认的json transformer不关心该参数，
     *                    用户自定义转换报文时可以通过给method添加自定义注解来识别怎么转换
     * @param requestBody
     * @return
     */
    String transformRequest(Method method, Object requestBody);

    /**
     * 转换响应
     * @param method                请求方法
     * @param resultType            响应java类型
     * @param responseHeaders       响应头
     * @param responseBody          响应内容
     * @param <T>
     * @return
     */
    <T> T transformResponse(Method method, Type resultType, Map<String, String> responseHeaders, String responseBody);
}
