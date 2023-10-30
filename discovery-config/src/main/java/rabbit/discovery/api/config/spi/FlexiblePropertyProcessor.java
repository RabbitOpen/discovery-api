package rabbit.discovery.api.config.spi;

import rabbit.discovery.api.common.spi.SpringMvcPostBeanProcessor;
import rabbit.discovery.api.config.ValueChangeListener;
import rabbit.discovery.api.config.loader.ConfigLoaderUtil;

/**
 * 通过spi接口暴露给 spring mvc 容器
 */
public class FlexiblePropertyProcessor implements SpringMvcPostBeanProcessor {

    private ValueChangeListener valueChangeListener;

    @Override
    public void before(Object bean, String name) {
        ConfigLoaderUtil.getConfigLoader().injectProperty(bean, valueChangeListener);
    }

    public final void setValueChangeListener(ValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }
}
