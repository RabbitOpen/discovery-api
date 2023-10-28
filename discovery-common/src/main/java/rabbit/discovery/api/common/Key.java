package rabbit.discovery.api.common;

import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.utils.RsaUtils;
import rabbit.flt.common.utils.StringUtil;

import java.security.PublicKey;

public class Key {

    private long updateTime;

    private PublicKey publicKey;

    public Key(String hex) {
        if (StringUtil.isEmpty(hex)) {
            throw new DiscoveryException("公钥信息不能为空");
        }
        updateTime = System.currentTimeMillis();
        publicKey= RsaUtils.loadPublicKeyFromString(hex);
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
