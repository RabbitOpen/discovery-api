package rabbit.discovery.api.plugins.server.creator;

import rabbit.discovery.api.plugins.common.SpringBeanCreator;
import rabbit.discovery.api.plugins.server.filter.HttpServletFilter;

/**
 * spring boot 应用通过该creator创建 HttpServletFilter
 */
public class HttpServletFilterCreator implements SpringBeanCreator {

    @Override
    public String getBeanName() {
        return "HttpServletFilter";
    }

    @Override
    public Class<?> getBeanClass() {
        return HttpServletFilter.class;
    }

    @Override
    public boolean match() {
        try {
            Class.forName("javax.servlet.Filter");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
