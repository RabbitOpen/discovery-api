package rabbit.discovery.api.rest.reader;

import org.springframework.web.bind.annotation.DeleteMapping;
import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.rest.MappingReader;
import rabbit.flt.common.utils.CollectionUtil;
import rabbit.flt.common.utils.StringUtil;

import java.util.Arrays;
import java.util.List;

public class DeleteMappingReader extends MappingReader<DeleteMapping> {

    public DeleteMappingReader(DeleteMapping mapping) {
        super(mapping);
    }

    @Override
    protected List<String> getDeclaredPaths() {
        if (!StringUtil.isEmpty(mapping.name())) {
            return Arrays.asList(mapping.name());
        }
        if (!CollectionUtil.isEmpty(mapping.value())) {
            return Arrays.asList(mapping.value());
        }
        if (!CollectionUtil.isEmpty(mapping.path())) {
            return Arrays.asList(mapping.path());
        }
        return Arrays.asList("");
    }

    @Override
    protected HttpMethod getHttpMethod() {
        return HttpMethod.DELETE;
    }
}
