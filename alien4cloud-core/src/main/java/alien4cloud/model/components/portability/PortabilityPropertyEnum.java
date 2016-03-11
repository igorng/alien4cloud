package alien4cloud.model.components.portability;

public enum PortabilityPropertyEnum {

    IAASS("iaaSs"),
    ORCHESTRATORS("orchestrators"),
    SUPPORTED_ARTIFACTS("supported_artifacts");

    private String name;

    private PortabilityPropertyEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static String IAASS_KEY = IAASS.name;
    public static String ORCHESTRATORS_KEY = ORCHESTRATORS.name;
    public static String SUPPORTED_ARTIFACTS_KEY = SUPPORTED_ARTIFACTS.name;

}
