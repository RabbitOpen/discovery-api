package rabbit.discovery.api.common;

import rabbit.flt.common.utils.VersionUtils;

import static rabbit.flt.common.utils.StringUtils.isEmpty;

public class Environment {

    private String version;

    private static final Environment inst = new Environment();

    private Environment() {
    }

    public static String getVersion() {
        if (!isEmpty(inst.version)) {
            return inst.version;
        }
        inst.version = VersionUtils.getVersion("discovery.properties", "version");
        return inst.version;
    }
}
