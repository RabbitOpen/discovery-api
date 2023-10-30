package rabbit.discovery.api.starter;

import rabbit.flt.common.log.Logger;

public class LoggerProxy implements Logger {

    org.slf4j.Logger realLogger;

    public LoggerProxy(org.slf4j.Logger realLogger) {
        this.realLogger = realLogger;
    }

    @Override
    public boolean isTraceEnabled() {
        return realLogger.isTraceEnabled();
    }

    @Override
    public void trace(String s) {
        realLogger.trace(s);
    }

    @Override
    public void trace(String s, Object o) {
        realLogger.trace(s, o);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        realLogger.trace(s, o, o1);
    }

    @Override
    public void trace(String s, Object... objects) {
        realLogger.trace(s, objects);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        realLogger.trace(s, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return realLogger.isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        realLogger.debug(s);
    }

    @Override
    public void debug(String s, Object o) {
        realLogger.debug(s, o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        realLogger.debug(s, o, o1);
    }

    @Override
    public void debug(String s, Object... objects) {
        realLogger.debug(s, objects);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        realLogger.debug(s, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return realLogger.isInfoEnabled();
    }

    @Override
    public void info(String s) {
        realLogger.info(s);
    }

    @Override
    public void info(String s, Object o) {
        realLogger.info(s, o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        realLogger.info(s, o, o1);
    }

    @Override
    public void info(String s, Object... objects) {
        realLogger.info(s, objects);
    }

    @Override
    public void info(String s, Throwable throwable) {
        realLogger.info(s, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return realLogger.isWarnEnabled();
    }

    @Override
    public void warn(String s) {
        realLogger.warn(s);
    }

    @Override
    public void warn(String s, Object o) {
        realLogger.warn(s, o);
    }

    @Override
    public void warn(String s, Object... objects) {
        realLogger.warn(s, objects);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        realLogger.warn(s, o, o1);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        realLogger.warn(s, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return realLogger.isErrorEnabled();
    }

    @Override
    public void error(String s) {
        realLogger.error(s);
    }

    @Override
    public void error(String s, Object o) {
        realLogger.error(s, o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        realLogger.error(s, o, o1);
    }

    @Override
    public void error(String s, Object... objects) {
        realLogger.error(s, objects);
    }

    @Override
    public void error(String s, Throwable throwable) {
        realLogger.error(s, throwable);
    }
}
