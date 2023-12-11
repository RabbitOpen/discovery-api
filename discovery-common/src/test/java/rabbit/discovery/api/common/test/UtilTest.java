package rabbit.discovery.api.common.test;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.Environment;
import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.common.enums.Schema;
import rabbit.discovery.api.common.utils.HexUtils;
import rabbit.discovery.api.common.utils.JsonUtils;
import rabbit.discovery.api.common.utils.RsaUtils;
import rabbit.flt.common.utils.GZipUtils;
import rabbit.flt.common.utils.StringUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

@RunWith(JUnit4.class)
public class UtilTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void gzipTest() {
        String text = "abc";
        byte[] zipped = GZipUtils.zip(text.getBytes());
        byte[] bytes = GZipUtils.unzipIgnoreOriginalLength(zipped, 1);
        TestCase.assertEquals(text, new String(bytes));
        bytes = GZipUtils.unzipIgnoreOriginalLength(zipped, 1024);
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
        TestCase.assertEquals("/abc", node.getPath());

        node = new ServerNode("www.baidu.com:90");
        TestCase.assertEquals(Schema.HTTP, node.getSchema());
        TestCase.assertEquals(90, node.getPort());
        TestCase.assertEquals("www.baidu.com", node.getHost());
        TestCase.assertEquals("http://www.baidu.com:90", node.address());

        node = new ServerNode("https://www.baidu.com");
        TestCase.assertEquals(Schema.HTTPS, node.getSchema());
        TestCase.assertEquals(443, node.getPort());
        TestCase.assertEquals("www.baidu.com", node.getHost());
        TestCase.assertEquals("https://www.baidu.com", node.address());

        node = new ServerNode("www.baidu.com");
        TestCase.assertEquals(Schema.HTTP, node.getSchema());
        TestCase.assertEquals(80, node.getPort());
        TestCase.assertEquals("www.baidu.com", node.getHost());
        TestCase.assertEquals("http://www.baidu.com", node.address());

        node = new ServerNode("www.baidu.com//abc");
        TestCase.assertEquals(Schema.HTTP, node.getSchema());
        TestCase.assertEquals(80, node.getPort());
        TestCase.assertEquals("www.baidu.com", node.getHost());
        TestCase.assertEquals("http://www.baidu.com", node.address());
        TestCase.assertEquals("/abc", node.getPath());

        node = new ServerNode("www.baidu.com/abc");
        TestCase.assertEquals(Schema.HTTP, node.getSchema());
        TestCase.assertEquals(80, node.getPort());
        TestCase.assertEquals("www.baidu.com", node.getHost());
        TestCase.assertEquals("http://www.baidu.com", node.address());
        TestCase.assertEquals("/abc", node.getPath());

        node = new ServerNode("192.168.0.10:9898");
        TestCase.assertEquals(Schema.HTTP, node.getSchema());
        TestCase.assertEquals(9898, node.getPort());
        TestCase.assertEquals("192.168.0.10", node.getHost());
        TestCase.assertEquals("http://192.168.0.10:9898", node.address());
    }

    @Test
    public void versionTest() {
        String version = Environment.getVersion();
        TestCase.assertTrue(!StringUtils.isEmpty(version));
        TestCase.assertFalse(version.contains("${"));
    }

    @Test
    public void jsonTest() {
        Map<String, String> map = JsonUtils.readValue("{\"a\": \"b\"}", JsonUtils.constructMapType(HashMap.class, String.class, String.class));
        TestCase.assertTrue(map.getClass() == HashMap.class);
        TestCase.assertTrue(map.get("a").equals("b"));

        Set<String> set = JsonUtils.readValue("[\"a\",\"b\"]", JsonUtils.constructListType(HashSet.class, String.class));
        TestCase.assertTrue(set.getClass() == HashSet.class);
        TestCase.assertTrue(set.size() == 2);

        List<String> list = JsonUtils.readValue("[\"a\",\"b\"]", JsonUtils.constructListType(ArrayList.class, String.class));
        TestCase.assertTrue(list.getClass() == ArrayList.class);
        TestCase.assertTrue(list.size() == 2);
        TestCase.assertTrue("a".equals(list.get(0)));
        TestCase.assertTrue("b".equals(list.get(1)));
    }
}
