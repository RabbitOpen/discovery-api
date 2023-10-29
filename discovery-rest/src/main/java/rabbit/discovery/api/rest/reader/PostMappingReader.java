package rabbit.discovery.api.rest.reader;

import org.springframework.web.bind.annotation.PostMapping;
import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.rest.MappingReader;

import java.util.Arrays;
import java.util.List;

public class PostMappingReader extends MappingReader<PostMapping> {

    public PostMappingReader(PostMapping mapping) {
        super(mapping);
    }

    @Override
    protected List<String[]> getDeclaredPathGroups() {
        return Arrays.asList(new String[]{mapping.name()}, mapping.value(), mapping.path());
    }

    @Override
    protected HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }
}
