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
import java.util.concurrent.atomic.AtomicLong;

public class ReactorHttpClientManagerPlugin extends PerformancePlugin {

    private String attachmentName = "traceData";

    @Override
    public Object[] before(Object target, Method method, Object[] args) {
        if (!isTraceOpened()) {
            return args;
        }
        if ("doRequest".equals(method.getName())) {
            // 开启了trace
            super.before(target, method, args);
            MethodStackInfo stackInfo = TraceContext.getStackInfo(method);
            HttpRequest request = (HttpRequest) args[0];
            if (null != stackInfo) {
                stackInfo.getTraceData().setNodeName("doHttpRequest");
                request.addAttachment(attachmentName, stackInfo.getTraceData());
            }
        } else if ("getHttpClient".equals(method.getName())) {
            // 添加链路追踪头
            HttpRequest request = (HttpRequest) args[0];
            request.setHeader(Headers.TRACE_ID, TraceContext.getTraceId());
            request.setHeader(Headers.SPAN_ID, getSpanId());
            request.setHeader(Headers.SOURCE_APP, AbstractConfigFactory.getConfig().getApplicationCode());
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
                rabbit.flt.common.trace.io.HttpResponse out = new rabbit.flt.common.trace.io.HttpResponse();
                HttpResponse response = (HttpResponse) args[2];
                out.setStatusCode(response.getStatusCode());
                out.getHeaders().putAll(response.getHeaders());
                traceData.setCost(System.currentTimeMillis() - traceData.getRequestTime());
                traceData.setHttpResponse(out);
                super.handleTraceData(traceData);
                return body;
            });
        }
        return super.after(objectEnhanced, method, args, result);
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

    private String getSpanId() {
        String rootSpanId = TraceContext.getRootSpanId();
        AtomicLong counter = TraceContext.getSpanIdChildCounter(rootSpanId);
        // 直接使用
        return rootSpanId + "-" + counter.get();
    }
}
