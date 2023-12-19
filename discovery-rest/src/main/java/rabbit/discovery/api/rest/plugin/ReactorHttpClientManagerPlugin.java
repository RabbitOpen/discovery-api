package rabbit.discovery.api.rest.plugin;

import rabbit.discovery.api.rest.http.HttpRequest;
import rabbit.discovery.api.rest.http.HttpResponse;
import rabbit.flt.common.AbstractConfigFactory;
import rabbit.flt.common.Headers;
import rabbit.flt.common.context.TraceContext;
import rabbit.flt.common.trace.MethodStackInfo;
import rabbit.flt.common.trace.TraceData;
import rabbit.flt.plugins.common.plugin.PerformancePlugin;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

public class ReactorHttpClientManagerPlugin extends PerformancePlugin {

    private String attachmentName = "traceData";

    @Override
    public Object[] before(Object target, Method method, Object[] args) {
        if (!isTraceOpened()) {
            return args;
        }
        if ("doRequest".equals(method.getName())) {
            super.before(target, method, args);
            // 开启了trace
            HttpRequest request = (HttpRequest) args[0];
            // 添加链路追踪头
            request.setHeader(Headers.TRACE_ID, TraceContext.getTraceId());
            request.setHeader(Headers.SPAN_ID, TraceContext.getRootSpanId());
            request.setHeader(Headers.SOURCE_APP, AbstractConfigFactory.getConfig().getApplicationCode());
            MethodStackInfo stackInfo = TraceContext.getStackInfo(method);
            if (null != stackInfo) {
                stackInfo.getTraceData().setNodeName("doHttpRequest");
                request.addAttachment(attachmentName, stackInfo.getTraceData());
            }
        }
        return args;
    }

    @Override
    public Object after(Object objectEnhanced, Method method, Object[] args, Object result) {
        if (isTraceOpened() && "exchange".equals(method.getName())) {
            HttpRequest request = (HttpRequest) args[0];
            TraceData traceData = request.getAttachment(attachmentName);
            rabbit.flt.common.trace.io.HttpRequest input = new rabbit.flt.common.trace.io.HttpRequest();
            input.setRequestUri(request.getUri());
            input.setMethod(request.getHttpMethod().name());
            input.getRequestParameters().putAll(request.getQueryParameters());
            input.getHeaders().putAll(request.getHeaders());
            traceData.setHttpRequest(input);
            return ((Mono<String>) result).map(body -> {
                sendTraceDta(args[2], traceData);
                return body;
            }).switchIfEmpty(Mono.defer(() -> {
                sendTraceDta(args[2], traceData);
                return Mono.empty();
            }));
        }
        return super.after(objectEnhanced, method, args, result);
    }

    private void sendTraceDta(Object arg, TraceData traceData) {
        rabbit.flt.common.trace.io.HttpResponse out = new rabbit.flt.common.trace.io.HttpResponse();
        HttpResponse response = (HttpResponse) arg;
        out.setStatusCode(response.getStatusCode());
        out.getHeaders().putAll(response.getHeaders());
        traceData.setCost(System.currentTimeMillis() - traceData.getRequestTime());
        traceData.setHttpResponse(out);
        super.handleTraceData(traceData);
    }

    @Override
    public void doFinal(Object objectEnhanced, Method method, Object[] args, Object result) {
        if ("doRequest".equals(method.getName())) {
            super.doFinal(objectEnhanced, method, args, result);
        }
    }

    @Override
    protected void handleTraceData(TraceData traceData) {
        // do nothing, 异步发送
    }

}
