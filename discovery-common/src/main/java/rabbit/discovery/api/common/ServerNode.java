package rabbit.discovery.api.common;

import rabbit.discovery.api.common.enums.Schema;
import rabbit.discovery.api.common.exception.DiscoveryException;

import java.net.URI;

import static rabbit.discovery.api.common.enums.Schema.HTTP;
import static rabbit.discovery.api.common.enums.Schema.HTTPS;
import static rabbit.flt.common.utils.StringUtils.isEmpty;

public class ServerNode {

    private Schema schema;

    private String host;

    private int port;

    public ServerNode(String address) {
        try {
            URI uri = new URI(address);
            setSchema(uri);
            setHost(uri);
            setPort(uri);
        } catch (Exception e) {
            throw new DiscoveryException(e);
        }
    }

    public ServerNode(String host, int port) {
        this(host, port, HTTP);
    }

    public ServerNode(String host, int port, Schema schema) {
        this.schema = schema;
        this.host = host;
        this.port = port;
    }

    public Schema getSchema() {
        return schema;
    }

    private void setSchema(URI uri) {
        if (null == uri.getScheme()) {
            this.schema = HTTP;
        } else {
            this.schema = Schema.valueOf(uri.getScheme());
        }
    }

    public String getHost() {
        return host;
    }

    private void setHost(URI uri) {
        if (isEmpty(uri.getHost())) {
            this.host = uri.getPath();
        } else {
            this.host = uri.getHost();
        }
    }

    public int getPort() {
        return port;
    }

    private void setPort(URI uri) {
        if (-1 != uri.getPort()) {
            this.port = uri.getPort();
        } else {
            this.port = this.schema == HTTPS ? 443 : 80;
        }
    }

    public boolean isHttps() {
        return HTTPS == this.schema;
    }
}
