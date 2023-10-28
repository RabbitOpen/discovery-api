package rabbit.discovery.api.common.protocol;

public class PrivilegeData {

    private byte[] compressedPrivileges;

    private int plainDataLength;

    public byte[] getCompressedPrivileges() {
        return compressedPrivileges;
    }

    public void setCompressedPrivileges(byte[] compressedPrivileges) {
        this.compressedPrivileges = compressedPrivileges;
    }

    public int getPlainDataLength() {
        return plainDataLength;
    }

    public void setPlainDataLength(int plainDataLength) {
        this.plainDataLength = plainDataLength;
    }
}
