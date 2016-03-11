package alien4cloud.model.components.portability;

public enum PortabilityPropertyEnum {
    IAASS("iaaSs"), ORCHESTRATORS("orchestrators"), RUNTIME_PACKAGES("runtimePackages");

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

    public static PortabilityPropertyEnum fromName(String name) {
        for (PortabilityPropertyEnum e : PortabilityPropertyEnum.values()) {
            if (e.name.equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static String IAASS_KEY = IAASS.name;
    public static String ORCHESTRATORS_KEY = ORCHESTRATORS.name;
    public static String SUPPORTED_ARTIFACTS_KEY = SUPPORTED_ARTIFACTS.name;
    public static String RUNTIME_PACKAGES_KEY = RUNTIME_PACKAGES.name;

}
