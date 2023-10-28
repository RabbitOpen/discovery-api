package rabbit.discovery.api.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.rpc.ProtocolService;
import rabbit.discovery.api.common.utils.RsaUtils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static rabbit.discovery.api.common.ApiProtocolHelper.getSignatureMap;

public class PublicKeyManager {

    private Logger logger = LoggerFactory.getLogger("publicKeyManager");

    private Configuration configuration;

    // 协议服务
    private ProtocolService protocolService;

    private PrivateKey privateKey;

    private ReentrantLock lock = new ReentrantLock();

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
            Map<String, String> signatureMap = getSignatureMap(applicationCode, privateKey);
            PublicKeyDesc publicKey = getProtocolService().getPublicKey(code, signatureMap);
            if (null != publicKey) {
                logger.info("public key[{}] loading success, version is {}", code, publicKey.getKeyVersion());
                return new Key(publicKey.getPublicKey());
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

    /**
     * 获取协议服务
     *
     * @return
     */
    private ProtocolService getProtocolService() {
        try {
            lock.lock();
            if (null == protocolService) {
                protocolService = RequestFactory.proxy(ProtocolService.class, configuration);
                privateKey = RsaUtils.loadPrivateKeyFromString(configuration.getPrivateKey().trim());
            }
            return protocolService;
        } finally {
            lock.unlock();
        }
    }

    public static void setConfiguration(Configuration configuration) {
        keyManager.configuration = configuration;
    }
}
