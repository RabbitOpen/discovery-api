package rabbit.discovery.api.common;

public class PublicKeyDesc {

    private String publicKey;

    private long keyVersion;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public long getKeyVersion() {
        return keyVersion;
    }

    public void setKeyVersion(long keyVersion) {
        this.keyVersion = keyVersion;
    }
}
