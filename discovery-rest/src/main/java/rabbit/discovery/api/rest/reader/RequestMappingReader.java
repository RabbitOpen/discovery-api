package rabbit.discovery.api.rest.reader;

import org.springframework.web.bind.annotation.RequestMapping;
import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.rest.MappingReader;
import rabbit.flt.common.utils.CollectionUtils;
import rabbit.flt.common.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

public class RequestMappingReader extends MappingReader<RequestMapping> {

    public RequestMappingReader(RequestMapping mapping) {
        super(mapping);
    }

    @Override
    protected List<String> getDeclaredPaths() {
        if (!StringUtils.isEmpty(mapping.name())) {
            return Arrays.asList(mapping.name());
        }
        if (!CollectionUtils.isEmpty(mapping.value())) {
            return Arrays.asList(mapping.value());
        }
        if (!CollectionUtils.isEmpty(mapping.path())) {
            return Arrays.asList(mapping.path());
        }
        return Arrays.asList("");
    }

    @Override
    protected HttpMethod getHttpMethod() {
        if (CollectionUtils.isEmpty(mapping.method()))  {
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
