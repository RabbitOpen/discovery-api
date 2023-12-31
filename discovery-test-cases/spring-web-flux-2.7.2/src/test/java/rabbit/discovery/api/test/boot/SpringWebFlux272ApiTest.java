package rabbit.discovery.api.test.boot;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import rabbit.discovery.api.test.CoreCases;
import rabbit.discovery.api.test.HttpRequestInterceptor;
import rabbit.discovery.api.test.TestLoadBalancer;
import rabbit.discovery.api.test.spi.TestTraceDataHandler;
import rabbit.flt.common.trace.TraceData;
import rabbit.flt.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringWebFluxEntry.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import({TestLoadBalancer.class, HttpRequestInterceptor.class})
public class SpringWebFlux272ApiTest {

    @Autowired
    ApplicationContext applicationContext;

    private String restApiCaseTraceId;

    private String monoRetryCaseTraceId;

    /**
     * discovery-rest 可单独作为api和open api的客户端使用
     * 由于没有Discovery能力，所以需要自己实现LoadBalancer
     * 或者 自己启动DiscoveryService
     */
    @Test
    public void springWebFlux272Test() throws Exception {
        TestLoadBalancer balancer = applicationContext.getBean(TestLoadBalancer.class);
        balancer.setPort(1802);

        // 监控trace
        Map<String, List<TraceData>> cache = new HashMap<>();
        Semaphore semaphore = new Semaphore(0);
        TestTraceDataHandler.setRealHandler(list -> list.forEach(t -> {
            cache.computeIfAbsent(t.getTraceId(), traceId -> new ArrayList<>()).add(t);
            if ("restApiCase".equals(t.getNodeName())) {
                restApiCaseTraceId = t.getTraceId();
            }
            if ("monoRetryCase".equals(t.getNodeName())) {
                monoRetryCaseTraceId = t.getTraceId();
            }
            if (!StringUtils.isEmpty(restApiCaseTraceId) && 13 == cache.get(restApiCaseTraceId).size()
                    && !StringUtils.isEmpty(monoRetryCaseTraceId) && 31 == cache.get(monoRetryCaseTraceId).size()) {
                // restApiCase下所有的trace都上报完毕
                semaphore.release();
            }
        }));

        CoreCases cases = new CoreCases();
        cases.configLoadCase(applicationContext);
        cases.openApiCase(applicationContext);
        cases.restApiCase(applicationContext);
        cases.simpleDataTest(applicationContext);
        cases.retryCase(applicationContext);
        cases.monoRestApiCase(applicationContext);
        cases.monoRetryCase(applicationContext);
        cases.reportServiceCase();
        cases.authorizationUrlCase(applicationContext);
        cases.webFluxTraceEnhanced();


        semaphore.acquire();
        Map<String, TraceData> traceMap = new HashMap<>();
        cache.get(restApiCaseTraceId).forEach(traceData -> traceMap.put(traceData.getSpanId(), traceData));
        TestCase.assertEquals("restApiCase", traceMap.get("0").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-0").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-1").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-2").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-3").getNodeName());
        TestCase.assertEquals("/rest/get/{name}/{age}", traceMap.get("0-0-0").getNodeName());
        TestCase.assertEquals("/rest/get/{name}/{age}", traceMap.get("0-1-0").getNodeName());
        TestCase.assertEquals("/rest/get/{name}/{age}", traceMap.get("0-2-0").getNodeName());
        TestCase.assertEquals("/rest/get/{name}/{age}", traceMap.get("0-3-0").getNodeName());
        TestCase.assertEquals("getUser", traceMap.get("0-0-0-0").getNodeName());
        TestCase.assertEquals("getUser", traceMap.get("0-1-0-0").getNodeName());
        TestCase.assertEquals("getUser", traceMap.get("0-2-0-0").getNodeName());
        TestCase.assertEquals("getUser", traceMap.get("0-3-0-0").getNodeName());

        traceMap.clear();
        List<TraceData> dataList = cache.get(monoRetryCaseTraceId);
        dataList.forEach(traceData -> traceMap.put(traceData.getSpanId(), traceData));
        TestCase.assertEquals(31, traceMap.size());

        TestCase.assertEquals("monoRetryCase", traceMap.get("0").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-0").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-1").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-2").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-3").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-4").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-5").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-6").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-7").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-8").getNodeName());
        TestCase.assertEquals("doHttpRequest", traceMap.get("0-9").getNodeName());

        TestCase.assertEquals("/rest/retry/{time}", traceMap.get("0-0-0").getNodeName());
        TestCase.assertEquals("/rest/retry/{time}", traceMap.get("0-1-0").getNodeName());
        TestCase.assertEquals("/rest/retry/{time}", traceMap.get("0-2-0").getNodeName());
        TestCase.assertEquals("/rest/retry/{time}", traceMap.get("0-3-0").getNodeName());
        TestCase.assertEquals("/rest/retry/{time}", traceMap.get("0-4-0").getNodeName());
        TestCase.assertEquals("/open/retry/{time}", traceMap.get("0-5-0").getNodeName());
        TestCase.assertEquals("/open/retry/{time}", traceMap.get("0-6-0").getNodeName());
        TestCase.assertEquals("/open/retry/{time}", traceMap.get("0-7-0").getNodeName());
        TestCase.assertEquals("/open/retry/{time}", traceMap.get("0-8-0").getNodeName());
        TestCase.assertEquals("/open/retry/{time}", traceMap.get("0-9-0").getNodeName());

        TestCase.assertEquals("retry", traceMap.get("0-0-0-0").getNodeName());
        TestCase.assertEquals("retry", traceMap.get("0-1-0-0").getNodeName());
        TestCase.assertEquals("retry", traceMap.get("0-2-0-0").getNodeName());
        TestCase.assertEquals("retry", traceMap.get("0-3-0-0").getNodeName());
        TestCase.assertEquals("retry", traceMap.get("0-4-0-0").getNodeName());
        TestCase.assertEquals("retry", traceMap.get("0-5-0-0").getNodeName());
        TestCase.assertEquals("retry", traceMap.get("0-6-0-0").getNodeName());
        TestCase.assertEquals("retry", traceMap.get("0-7-0-0").getNodeName());
        TestCase.assertEquals("retry", traceMap.get("0-8-0-0").getNodeName());
        TestCase.assertEquals("retry", traceMap.get("0-9-0-0").getNodeName());
        TestTraceDataHandler.resetHandler();
    }
}


