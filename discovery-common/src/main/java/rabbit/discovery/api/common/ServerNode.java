package rabbit.discovery.api.common;

import rabbit.discovery.api.common.enums.Schema;
import rabbit.discovery.api.common.utils.PathParser;
import rabbit.flt.common.utils.StringUtils;

import static rabbit.discovery.api.common.enums.Schema.HTTP;
import static rabbit.discovery.api.common.enums.Schema.HTTPS;

public class ServerNode {

    private Schema schema;

    private String host;

    private int port;

    private String path;

    private static final String HTTP_KEY_WORD = "http://";

    private static final String HTTPS_KEY_WORD = "https://";

    /**
     * constructor
     * @param address 合法的http地址信息
     *          http://www.baidu.com
     *          https://www.baidu.com:998
     *          https://www.baidu.com:998/abc
     *          https://www.baidu.com:998/abc?name=z
     *          www.baidu.com
     *          www.baidu.com:12801
     */
    public ServerNode(String address) {
        this();
        this.setSchema(address);
        this.resolve(address);
    }

    public ServerNode() {
    }

    public ServerNode(String host, int port) {
        this(host, port, HTTP);
    }

    public ServerNode(String host, int port, Schema schema) {
        this.schema = schema;
        this.host = host;
        this.port = port;
    }

    public String address() {
        boolean ignore = (80 == port && HTTP == schema) || (443 == port && HTTPS == schema);
        return new StringBuilder(schema.name().toLowerCase()).append("://").append(host)
                .append(ignore ? "" : ":".concat(Integer.toString(port)))
                .toString();
    }

    public Schema getSchema() {
        return schema;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isHttps() {
        return HTTPS == this.schema;
    }

    public String getPath() {
        return path;
    }

    private void setPath(String path) {
        if (!StringUtils.isEmpty(path)) {
            this.path = PathParser.removeRepeatedSeparator(path);
            if (this.path.contains("?")) {
                this.path = this.path.substring(0, path.indexOf('?') - 1);
            }
            if ("/".equals(this.path)) {
                this.path = "";
            }
        }
    }

    private void setSchema(String address) {
        if (address.startsWith(HTTPS_KEY_WORD)) {
            this.schema = HTTPS;
        } else {
            this.schema = HTTP;
        }
    }

    private void resolve(String address) {
        String uri = cutProtocol(address);
        if (uri.contains("/")) {
            int index = uri.indexOf('/');
            setPath(uri.substring(index));
            uri = uri.substring(0, index);
        }
        if (uri.contains(":")) {
            host = uri.split(":")[0];
            port = Integer.parseInt(uri.split(":")[1]);
        } else {
            host = uri;
            port = isHttps() ? 443 : 80;
        }
    }

    /**
     * 去掉协议
     *
     * @param address
     * @return
     */
    private String cutProtocol(String address) {
        if (address.startsWith(HTTP_KEY_WORD)) {
            return address.substring(HTTP_KEY_WORD.length());
        }
        if (address.startsWith(HTTPS_KEY_WORD)) {
            return address.substring(HTTPS_KEY_WORD.length());
        }
        return address;
    }

}
