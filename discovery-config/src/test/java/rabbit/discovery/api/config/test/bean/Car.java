package rabbit.discovery.api.config.test.bean;

import java.util.Set;

public class Car {

    private String id;

    private String type;

    private Set<String> owner;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Set<String> getOwner() {
        return owner;
    }
}
