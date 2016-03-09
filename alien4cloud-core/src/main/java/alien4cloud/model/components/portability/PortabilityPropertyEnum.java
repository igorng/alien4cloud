package alien4cloud.model.components.portability;

public enum PortabilityPropertyEnum {
    IAAS("iaaS");

    private String name;

    private PortabilityPropertyEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
