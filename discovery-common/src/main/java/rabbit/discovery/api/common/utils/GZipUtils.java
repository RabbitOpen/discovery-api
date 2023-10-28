package rabbit.discovery.api.common.utils;

import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.flt.common.utils.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;

public class GZipUtils {

    /**
     * 解压缩
     * @param data
     * @param stepSize
     * @return
     */
    public static byte[] decompressIgnoreOriginalLength(byte[] data, int stepSize) {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GZIPInputStream gzipIs = null;
        try {
            gzipIs = new GZIPInputStream(is);
            byte[] bytes = new byte[stepSize];
            while (true) {
                int read = gzipIs.read(bytes, 0, bytes.length);
                if (read > 0) {
                    os.write(bytes, 0, read);
                } else {
                    break;
                }
            }
            return os.toByteArray();
        } catch (Exception e) {
            throw new DiscoveryException(e);
        } finally {
            ResourceUtils.close(gzipIs);
            ResourceUtils.close(os);
            ResourceUtils.close(is);
        }
    }
}
