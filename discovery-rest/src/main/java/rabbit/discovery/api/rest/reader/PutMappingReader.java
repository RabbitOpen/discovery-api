package rabbit.discovery.api.rest.reader;

import org.springframework.web.bind.annotation.PutMapping;
import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.rest.MappingReader;
import rabbit.flt.common.utils.CollectionUtils;
import rabbit.flt.common.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

public class PutMappingReader extends MappingReader<PutMapping> {

    public PutMappingReader(PutMapping mapping) {
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
        return HttpMethod.PUT;
    }
}