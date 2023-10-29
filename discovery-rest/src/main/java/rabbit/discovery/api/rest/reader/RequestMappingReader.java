package rabbit.discovery.api.rest.reader;

import org.springframework.web.bind.annotation.RequestMapping;
import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.rest.MappingReader;
import rabbit.flt.common.utils.CollectionUtils;

import java.util.Arrays;
import java.util.List;

public class RequestMappingReader extends MappingReader<RequestMapping> {

    public RequestMappingReader(RequestMapping mapping) {
        super(mapping);
    }

    @Override
    protected List<String[]> getDeclaredPathGroups() {
        return Arrays.asList(new String[]{mapping.name()}, mapping.value(), mapping.path());
    }

    @Override
    protected HttpMethod getHttpMethod() {
        if (CollectionUtils.isEmpty(mapping.method())) {
            return HttpMethod.GET;
        } else {
            try {
                return HttpMethod.valueOf(mapping.method()[0].name());
            } catch (Exception e) {
                return HttpMethod.GET;
            }
        }
    }
}
