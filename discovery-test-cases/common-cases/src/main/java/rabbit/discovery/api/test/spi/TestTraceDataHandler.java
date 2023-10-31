package rabbit.discovery.api.test.spi;

import rabbit.flt.common.TraceDataHandler;
import rabbit.flt.common.trace.TraceData;

import java.util.List;

public class TestTraceDataHandler implements TraceDataHandler {

    private static TraceDataHandler realHandler;

    static  {
        resetHandler();
    }

    public static void setRealHandler(TraceDataHandler realHandler) {
        TestTraceDataHandler.realHandler = realHandler;
    }

    public static void resetHandler() {
        setRealHandler(list -> {
            //ignore
        });
    }

    @Override
    public void process(List<TraceData> list) {
        realHandler.process(list);
    }


    @Override
    public int getPriority() {
        return 1000;
    }
}
