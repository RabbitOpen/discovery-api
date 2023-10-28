package rabbit.discovery.api.rest.reader;

import org.springframework.web.bind.annotation.PostMapping;
import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.rest.MappingReader;

import java.util.Arrays;
import java.util.List;
import rabbit.flt.common.utils.CollectionUtils;
import rabbit.flt.common.utils.StringUtils;
public class PostMappingReader extends MappingReader<PostMapping> {

    public PostMappingReader(PostMapping mapping) {
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
        return HttpMethod.POST;
    }
}
