package rabbit.discovery.api.common.test;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.common.enums.Schema;
import rabbit.discovery.api.common.utils.GZipUtils;
import rabbit.discovery.api.common.utils.HexUtils;
import rabbit.discovery.api.common.utils.RsaUtils;
import rabbit.flt.rpc.common.GzipUtil;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

@RunWith(JUnit4.class)
public class UtilTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void gzipTest() {
        String text = "abc";
        byte[] compress = GzipUtil.compress(text.getBytes());
        byte[] bytes = GZipUtils.decompressIgnoreOriginalLength(compress, 1);
        TestCase.assertEquals(text, new String(bytes));
        bytes = GZipUtils.decompressIgnoreOriginalLength(compress, 1024);
        TestCase.assertEquals(text, new String(bytes));
    }

    @Test
    public void hexTest() {
        String hex = "FAB889923";
        byte[] bytes = HexUtils.toBytes(hex);
        TestCase.assertEquals("0".concat(hex), HexUtils.toHex(bytes));

        hex = "1FAB889923";
        bytes = HexUtils.toBytes(hex);
        TestCase.assertEquals(hex, HexUtils.toHex(bytes));
    }

    @Test
    public void rsaTest() {
        KeyPair keyPair = RsaUtils.generateKeyPair(512);
        String privateKeyStr = HexUtils.toHex(keyPair.getPrivate().getEncoded());
        String publicKeyStr = HexUtils.toHex(keyPair.getPublic().getEncoded());
        logger.info("privateKeyStr: {}", privateKeyStr);
        logger.info("publicKeyStr: {}", publicKeyStr);

        PrivateKey privateKey = RsaUtils.loadPrivateKeyFromString(privateKeyStr);
        PublicKey publicKey = RsaUtils.loadPublicKeyFromString(publicKeyStr);
        String plainText = "china";
        byte[] bytes = RsaUtils.encryptWithPublicKey(plainText, publicKey);
        TestCase.assertEquals(plainText, new String(RsaUtils.decryptWithPrivateKey(bytes, privateKey)));

        byte[] signBytes = RsaUtils.signWithPrivateKey(plainText, privateKey);
        TestCase.assertTrue(RsaUtils.verifyWithPublicKey(signBytes, plainText, publicKey));
        TestCase.assertFalse(RsaUtils.verifyWithPublicKey(signBytes, plainText + "1", publicKey));
    }

    @Test
    public void serverNodeTest() {
        ServerNode node = new ServerNode("https://192.168.0.1:10101//abc?a=1");
        TestCase.assertEquals(Schema.HTTPS, node.getSchema());
        TestCase.assertEquals(10101, node.getPort());
        TestCase.assertEquals("192.168.0.1", node.getHost());
        TestCase.assertEquals("/abc", node.getPath());
        TestCase.assertEquals("https://192.168.0.1:10101/abc", node.address().concat(node.getPath()));
    }
}
