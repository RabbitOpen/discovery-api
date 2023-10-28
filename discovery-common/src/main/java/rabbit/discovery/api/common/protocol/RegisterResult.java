package rabbit.discovery.api.common.protocol;

/**
 * 注册结果
 */
public class RegisterResult {

    private String id;

    private ApplicationMeta applicationMeta;

    private boolean success = true;

    private String message;

    public RegisterResult() {
    }

    private RegisterResult(String id, ApplicationMeta applicationMeta) {
        this();
        this.id = id;
        this.applicationMeta = applicationMeta;
        this.success = true;
    }

    public static RegisterResult success(String id, ApplicationMeta meta) {
        return new RegisterResult(id, meta);
    }

    public static RegisterResult fail(String message) {
        RegisterResult result = new RegisterResult();
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ApplicationMeta getApplicationMeta() {
        return applicationMeta;
    }

    public void setApplicationMeta(ApplicationMeta applicationMeta) {
        this.applicationMeta = applicationMeta;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
