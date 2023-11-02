package rabbit.discovery.api.test.spi;

import rabbit.flt.common.spi.ClassProxyListener;

import java.util.HashSet;
import java.util.Set;

public class TestClassProxyListener implements ClassProxyListener {

    private static Set<String> classList = new HashSet<>();

    @Override
    public void onProxy(String s) {
        classList.add(s);
    }

    public static Set<String> getClassList() {
        return classList;
    }
}
