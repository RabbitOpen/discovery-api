package rabbit.discovery.api.common.global;

import rabbit.discovery.api.common.protocol.ApplicationMeta;

/**
 * 应用meta信息缓存
 */
public class ApplicationMetaCache {

    private static final ApplicationMetaCache cache = new ApplicationMetaCache();

    private ApplicationMeta meta = new ApplicationMeta();

    private ApplicationMetaCache() {}

    public static ApplicationMeta getApplicationMeta() {
        return cache.meta;
    }

    public static void setApplicationMeta(ApplicationMeta meta) {
        cache.meta = meta;
    }
}
