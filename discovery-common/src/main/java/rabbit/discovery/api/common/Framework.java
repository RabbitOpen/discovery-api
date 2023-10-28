package rabbit.discovery.api.common;

import rabbit.discovery.api.common.exception.DiscoveryException;

public class Framework {

    public static final Framework SPRING_BOOT = new Framework();

    public static final Framework SPRING_MVC = new Framework();

    private static Framework frameWork;

    private Framework() {}

    /**
     * 获取当前框架
     *
     * @return
     */
    public static Framework getFrameWork() {
        return frameWork;
    }

    /**
     * 判断是不是spring mvc
     *
     * @return
     */
    public static boolean isSpringMvcFrameWork() {
        return SPRING_MVC == frameWork;
    }

    /**
     * @param frameWork
     */
    public static void setFrameWork(Framework frameWork) {
        if (null != getFrameWork() && frameWork != getFrameWork()) {
            throw new DiscoveryException("framework type is unalterable!");
        }
        Framework.frameWork = frameWork;
    }
}
