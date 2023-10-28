package rabbit.discovery.api.common;

import rabbit.flt.common.utils.VersionUtils;

import static rabbit.flt.common.utils.StringUtils.isEmpty;

public class Environment {

    private static String version;

    public static String getVersion() {
        if (!isEmpty(version)) {
            return version;
        }
        version = VersionUtils.getVersion("discovery.properties", "version");
        return version;
    }
}
