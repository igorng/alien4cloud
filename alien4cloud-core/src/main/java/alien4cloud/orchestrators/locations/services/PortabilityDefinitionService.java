package alien4cloud.orchestrators.locations.services;

import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.elasticsearch.common.collect.Maps;
import org.springframework.stereotype.Component;

import alien4cloud.model.components.PropertyDefinition;
import alien4cloud.model.components.portability.PortabilityPropertyEnum;

/**
 * A {@link PropertyDefinition} store for portability indicators.
 * <p>
 * TODO: parse a YAML file to constitute the map.
 */
@Component
@Log4j
public class PortabilityDefinitionService {

    private static Map<PortabilityPropertyEnum, PropertyDefinition> PORTABILITY_DEFINITIONS;

    static {
        PORTABILITY_DEFINITIONS = Maps.newHashMap();

        PropertyDefinition runtimePackagesDefinition = new PropertyDefinition();
        runtimePackagesDefinition.setType("list");
        PropertyDefinition stringDefinition = new PropertyDefinition();
        stringDefinition.setType("string");
        runtimePackagesDefinition.setEntrySchema(stringDefinition);
        runtimePackagesDefinition.setDescription("Installed packages on a compute / runtime requirements for a component");
        PORTABILITY_DEFINITIONS.put(PortabilityPropertyEnum.RUNTIME_PACKAGES, runtimePackagesDefinition);
    }

    /**
     * For a given portability indicator, provide the right {@link PropertyDefinition}.
     */
    public PropertyDefinition getPropertyDefinition(String portabilityIndicatorKey) {
        PortabilityPropertyEnum k = PortabilityPropertyEnum.fromName(portabilityIndicatorKey);
        if (k == null) {
            throw new NullPointerException(String.format("The PortabilityPropertyEnum entry <%s> does not exists !", portabilityIndicatorKey));
        }
        return PORTABILITY_DEFINITIONS.get(k);
    }

}
