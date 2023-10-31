package rabbit.discovery.api.test.spi;

import rabbit.flt.common.Metrics;
import rabbit.flt.common.MetricsDataHandler;

/**
 * 忽略metrics指标上报
 */
public class EmptyMetricsDataHandler implements MetricsDataHandler {

    @Override
    public boolean handle(Metrics metrics) {
        return true;
    }

    @Override
    public int getPriority() {
        return 1000;
    }
}
