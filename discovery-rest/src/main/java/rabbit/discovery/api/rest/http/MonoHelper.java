package rabbit.discovery.api.rest.http;

import rabbit.discovery.api.rest.HttpRequestExecutor;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 滞后加载 reactor.core.publisher.Mono
 */
public class MonoHelper {

    private static final MonoHelper inst = new MonoHelper();

    private MonoHelper() {
    }

    /**
     * 处理异步结果
     *
     * @param request
     * @param response
     * @param resultType
     * @param executor
     * @return
     */
    public static Mono<Object> handleAsyncResponse(HttpRequest request, HttpResponse response,
                                                   ParameterizedType resultType, HttpRequestExecutor executor) {
        Mono<String> asyncResult = (Mono<String>) response.getData();
        Type rawType = resultType.getActualTypeArguments()[0];
        return asyncResult.flatMap(body -> inst.getMonoObject(request, response, executor, rawType, body))
                .switchIfEmpty(Mono.defer(() -> inst.getMonoObject(request, response, executor, rawType, null)));
    }

    private Mono<Object> getMonoObject(HttpRequest request, HttpResponse response,
                                       HttpRequestExecutor executor, Type rawType, String body) {
        if (void.class == rawType || Void.class == rawType) {
            return Mono.empty();
        }
        Object data = executor.readResponseByType(request, response, rawType, body);
        return null == data ? Mono.empty() : Mono.just(data);
    }
}
