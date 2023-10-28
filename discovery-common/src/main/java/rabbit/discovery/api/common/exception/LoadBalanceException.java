package rabbit.discovery.api.common.exception;

public class LoadBalanceException extends DiscoveryException {

    public LoadBalanceException(String application, String group) {
        super("获取应用[".concat(application).concat("]-[").concat(group).concat("]服务实例清单信息失败"));
    }
}
