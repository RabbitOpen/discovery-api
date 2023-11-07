package rabbit.discovery.api.starter.test;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.starter.LoggerProxy;

@RunWith(JUnit4.class)
public class LoggerProxyTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void loggerTest() {
        LoggerProxy proxy = new LoggerProxy(logger);
        TestCase.assertEquals(logger.isDebugEnabled(), proxy.isDebugEnabled());
        TestCase.assertEquals(logger.isTraceEnabled(), proxy.isTraceEnabled());
        TestCase.assertEquals(logger.isInfoEnabled(), proxy.isInfoEnabled());
        TestCase.assertEquals(logger.isWarnEnabled(), proxy.isWarnEnabled());
        TestCase.assertEquals(logger.isErrorEnabled(), proxy.isErrorEnabled());
        proxy.info("info");
        proxy.debug("debug");
        proxy.warn("warn");
        proxy.trace("trace");
        proxy.error("error");
        proxy.info("info: {}", "test error");
        proxy.debug("debug: {}", "test error");
        proxy.warn("warn: {}", "test error");
        proxy.trace("trace: {}", "test error");
        proxy.error("error: {}", "test error");
    }
}
