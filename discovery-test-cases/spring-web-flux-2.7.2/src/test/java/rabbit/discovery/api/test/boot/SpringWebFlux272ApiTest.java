package rabbit.discovery.api.test.boot;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ApplicationContext applicationContext;

    private String targetTraceId;
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
                targetTraceId = t.getTraceId();
            }
            if (!StringUtils.isEmpty(targetTraceId) && 10 == cache.get(targetTraceId).size()) {
                // restApiCase下所有的trace都上报完毕
                semaphore.release();
            }
        }));

        CoreCases cases = new CoreCases();
        cases.configLoadCase(applicationContext);
        cases.monoRetryCase(applicationContext);
        cases.openApiCase(applicationContext);
        cases.restApiCase(applicationContext);
        cases.monoRestApiCase(applicationContext);
        cases.reportServiceCase();
        cases.authorizationUrlCase(applicationContext);
        cases.webFluxTraceEnhanced();


        semaphore.acquire();
        Map<String, TraceData> restApiCaseTrace = new HashMap<>();
        cache.get(targetTraceId).forEach(traceData -> restApiCaseTrace.put(traceData.getSpanId(), traceData));
        TestCase.assertEquals("restApiCase", restApiCaseTrace.get("0").getNodeName());
        TestCase.assertEquals("doHttpRequest", restApiCaseTrace.get("0-0").getNodeName());
        TestCase.assertEquals("doHttpRequest", restApiCaseTrace.get("0-1").getNodeName());
        TestCase.assertEquals("doHttpRequest", restApiCaseTrace.get("0-2").getNodeName());
        TestCase.assertEquals("/rest/get/{name}/{age}", restApiCaseTrace.get("0-0-0").getNodeName());
        TestCase.assertEquals("/rest/get/{name}/{age}", restApiCaseTrace.get("0-1-0").getNodeName());
        TestCase.assertEquals("/rest/get/{name}/{age}", restApiCaseTrace.get("0-2-0").getNodeName());
        TestCase.assertEquals("getUser", restApiCaseTrace.get("0-0-0-0").getNodeName());
        TestCase.assertEquals("getUser", restApiCaseTrace.get("0-1-0-0").getNodeName());
        TestCase.assertEquals("getUser", restApiCaseTrace.get("0-2-0-0").getNodeName());
        TestTraceDataHandler.resetHandler();
    }
}


