package rabbit.discovery.api.common.protocol;

/**
 * 注册结果
 */
public class RegisterResult {

    private ApplicationMeta applicationMeta;

    private boolean success = true;

    private String message;

    public RegisterResult() {
    }

    private RegisterResult(ApplicationMeta applicationMeta) {
        this();
        setApplicationMeta(applicationMeta);
        setSuccess(true);
    }

    public static RegisterResult success(ApplicationMeta meta) {
        return new RegisterResult(meta);
    }

    public static RegisterResult fail(String message) {
        RegisterResult result = new RegisterResult();
        result.setMessage(message);
        result.setSuccess(false);
        return result;
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
