package rabbit.discovery.api.starter.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.Framework;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.plugins.server.filter.HttpServletFilter;
import rabbit.discovery.api.starter.ClassUtils;

import javax.servlet.*;
import java.lang.reflect.Field;
import java.util.EnumSet;

public class SpringMvcStartListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("spring mvc starter listener is started!");
        Framework.setFrameWork(Framework.SPRING_MVC);
        ClassUtils.doProxy();
        registerAuthenticationFilter(getServletContext(sce));
    }

    private ServletContext getServletContext(ServletContextEvent event) {
        try {
            ServletContext servletContext = event.getServletContext();
            String targetContext = "org.apache.catalina.core.StandardContext$NoPluggabilityServletContext";
            String name = servletContext.getClass().getName();
            if (targetContext.equals(name)) {
                Field field = servletContext.getClass().getDeclaredField("sc");
                field.setAccessible(true);
                return (ServletContext) field.get(servletContext);
            }
            return servletContext;
        } catch (Exception e) {
            throw new DiscoveryException(e);
        }
    }

    /**
     * 注册认证过滤器
     * @param context
     */
    private void registerAuthenticationFilter(ServletContext context) {
        HttpServletFilter filter = new HttpServletFilter();
        String filterName = "servletAuthenticationFilter";
        FilterRegistration.Dynamic registration = context.addFilter(filterName, filter);
        filter.setSupplier(ClassUtils.getSupplier());
        registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("spring mvc starter listener is closed!");
    }
}
