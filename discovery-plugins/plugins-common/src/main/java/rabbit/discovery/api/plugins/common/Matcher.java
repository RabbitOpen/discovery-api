package rabbit.discovery.api.plugins.common;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public interface Matcher {

    /**
     * 匹配的class
     * @return
     */
    ElementMatcher.Junction<TypeDescription> classMatcher();

    /**
     * 方法匹配
     * @param typeDescription
     * @return
     */
    ElementMatcher.Junction methodMatcher(TypeDescription typeDescription);

    /**
     * 适用的插件
     * @param <T>
     * @return
     */
    <T extends Plugin> T getPlugin();
}
