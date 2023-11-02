package rabbit.discovery.api.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.flt.common.spi.ClassProxyListener;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

class DefaultClassProxyListener implements ClassProxyListener {

    private Logger logger = LoggerFactory.getLogger("transformer");

    private List<DefaultClassProxyListener> listeners = new ArrayList<>();

    public DefaultClassProxyListener() {
        ServiceLoader.load(DefaultClassProxyListener.class).forEach(listeners::add);
    }
    @Override
    public void onProxy(String className) {
        logger.info("found target class: [{}]", className);
        listeners.forEach(l -> l.onProxy(className));
    }
}
