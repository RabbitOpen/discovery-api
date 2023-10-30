package rabbit.discovery.api.starter;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.utility.JavaModule;
import rabbit.discovery.api.common.SpringBeanSupplier;
import rabbit.discovery.api.plugins.common.Matcher;
import rabbit.discovery.api.plugins.common.Plugin;
import rabbit.discovery.api.plugins.common.plugin.DiscoveryPlugin;
import rabbit.discovery.api.starter.interceptor.DefaultMethodInterceptor;
import rabbit.discovery.api.starter.interceptor.MethodCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class DiscoveryClassTransformer implements AgentBuilder.Transformer {

    private List<Matcher> matchers;

    private SpringBeanSupplier supplier;

    private List<ClassProxyLogListener> listeners = new ArrayList<>();

    public DiscoveryClassTransformer(List<Matcher> matchers, SpringBeanSupplier supplier) {
        this.matchers = matchers;
        this.supplier = supplier;
        ServiceLoader.load(ClassProxyLogListener.class).forEach(listeners::add);
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
        DynamicType.Builder<?> typeBuilder = builder;
        listeners.forEach(l -> l.onProxy(typeDescription.getCanonicalName()));
        for (Matcher matcher : matchers) {
            if (!matcher.classMatcher().matches(typeDescription)) {
                continue;
            }
            Plugin plugin = matcher.getPlugin();
            if (plugin instanceof DiscoveryPlugin) {
                ((DiscoveryPlugin) plugin).setSupplier(supplier);
            }
            typeBuilder = typeBuilder.method(matcher.methodMatcher(typeDescription))
                    .intercept(MethodDelegation.withDefaultConfiguration()
                            .withBinders(Morph.Binder.install(MethodCallback.class))
                            .to(new DefaultMethodInterceptor(plugin)));
        }
        return typeBuilder;
    }
}
