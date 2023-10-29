package rabbit.discovery.api.rest.reader;

import org.springframework.web.bind.annotation.PatchMapping;
import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.rest.MappingReader;

import java.util.Arrays;
import java.util.List;

public class PatchMappingReader extends MappingReader<PatchMapping> {

    public PatchMappingReader(PatchMapping mapping) {
        super(mapping);
    }

    @Override
    protected List<String[]> getDeclaredPathGroups() {
        return Arrays.asList(new String[]{mapping.name()}, mapping.value(), mapping.path());
    }

    @Override
    protected HttpMethod getHttpMethod() {
        return HttpMethod.PATCH;
    }
}
