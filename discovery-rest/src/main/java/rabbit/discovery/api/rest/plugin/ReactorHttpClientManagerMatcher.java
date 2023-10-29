package rabbit.discovery.api.rest.plugin;

import rabbit.flt.bytebuddy.description.type.TypeDescription;
import rabbit.flt.bytebuddy.matcher.ElementMatcher;
import rabbit.flt.plugins.common.matcher.PerformanceMatcher;

import static rabbit.flt.bytebuddy.matcher.ElementMatchers.named;

public class ReactorHttpClientManagerMatcher extends PerformanceMatcher {

    @Override
    public ElementMatcher.Junction<TypeDescription> classMatcher() {
        return named("rabbit.discovery.api.rest.http.ReactorHttpClientManager");
    }

    @Override
    public ElementMatcher.Junction methodMatcher(TypeDescription typeDescription) {
        return named("doRequest").or(named("exchange")).or(named("getHttpClient"));
    }

    @Override
    public String getPluginClassName() {
        return ReactorHttpClientManagerPlugin.class.getName();
    }
}
