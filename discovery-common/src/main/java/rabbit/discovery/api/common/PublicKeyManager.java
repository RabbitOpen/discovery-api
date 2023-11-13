package rabbit.discovery.api.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.rpc.ProtocolServiceWrapper;

import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PublicKeyManager {

    private Logger logger = LoggerFactory.getLogger("publicKeyManager");

    private Configuration configuration;

    private Map<String, Key> cache = new ConcurrentHashMap<>();

    private static final PublicKeyManager keyManager = new PublicKeyManager();

    private PublicKeyManager() {
    }

    /**
     * 获取公钥
     *
     * @param applicationCode
     * @return
     */
    public static PublicKey getPublicKey(String applicationCode) {
        return keyManager.loadKeyFromCache(applicationCode).getPublicKey();
    }

    /**
     * 获取缓存中的公钥，如果不存在就从服务器下载
     *
     * @param applicationCode
     * @return
     */
    private Key loadKeyFromCache(String applicationCode) {
        return cache.computeIfAbsent(applicationCode, code -> {
            String publicKey = ProtocolServiceWrapper.getPublicKey(code);
            if (null != publicKey) {
                logger.info("public key[{}] is loaded", code);
                return new Key(publicKey);
            } else {
                throw new DiscoveryException("获取应用[".concat(code).concat("]公钥信息失败"));
            }
        });
    }

    /**
     * 刷新密钥
     *
     * @param applicationCode
     * @return
     */
    public static PublicKey refreshPublicKey(String applicationCode) {
        Key key = keyManager.loadKeyFromCache(applicationCode);
        if (System.currentTimeMillis() - key.getUpdateTime() > 10000) {
            // 10s 内不重复加载密钥
            keyManager.cache.remove(applicationCode);
        }
        return getPublicKey(applicationCode);
    }

    public static void setConfiguration(Configuration configuration) {
        if (null == keyManager.configuration) {
            keyManager.configuration = configuration;
            ProtocolServiceWrapper.init(configuration);
        }
    }
}
