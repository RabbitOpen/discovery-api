package rabbit.discovery.api.rest.reader;

import org.springframework.web.bind.annotation.DeleteMapping;
import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.rest.MappingReader;

import java.util.Arrays;
import java.util.List;

public class DeleteMappingReader extends MappingReader<DeleteMapping> {

    public DeleteMappingReader(DeleteMapping mapping) {
        super(mapping);
    }

    @Override
    protected List<String[]> getDeclaredPathGroups() {
        return Arrays.asList(new String[]{mapping.name()}, mapping.value(), mapping.path());
    }

    @Override
    protected HttpMethod getHttpMethod() {
        return HttpMethod.DELETE;
    }
}
