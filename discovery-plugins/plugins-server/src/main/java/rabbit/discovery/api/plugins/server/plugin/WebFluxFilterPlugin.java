package rabbit.discovery.api.plugins.server.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.WebFilter;
import rabbit.discovery.api.plugins.common.plugin.DiscoveryPlugin;
import rabbit.discovery.api.plugins.server.filter.WebFluxFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class WebFluxFilterPlugin extends DiscoveryPlugin {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // 拦截org.springframework.web.server.adapter.WebHttpHandlerBuilder.build方法
    @Override
    public boolean intercept(Method method, Object[] args, Object target) {
        try {
            Field field = target.getClass().getDeclaredField("filters");
            field.setAccessible(true);
            List<WebFilter> filters = (List<WebFilter>) field.get(target);
            if (filters.isEmpty() || !(filters.get(0) instanceof WebFluxFilter)) {
                WebFluxFilter webFluxFilter = new WebFluxFilter();
                webFluxFilter.setSupplier(getSupplier());
                filters.add(0, webFluxFilter);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return super.intercept(method, args, target);
    }
}
