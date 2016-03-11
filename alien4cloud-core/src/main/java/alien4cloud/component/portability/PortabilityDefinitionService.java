package alien4cloud.component.portability;

import alien4cloud.common.ParsedPropertiesDefinitions;
import alien4cloud.common.parser.PropertiesDefinitionYamlParser;
import alien4cloud.exception.NotFoundException;
import alien4cloud.model.components.PropertyDefinition;
import alien4cloud.tosca.parser.ParsingException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.collect.Maps;
import org.springframework.stereotype.Component;

/**
 * A {@link PropertyDefinition} store for portability indicators.
 * 
 */
@Component
@Slf4j
public class PortabilityDefinitionService {

    @Inject
    private PropertiesDefinitionYamlParser parser;

    @Getter
    private Map<PortabilityPropertyEnum, PropertyDefinition> portabilityDefinitions;

    private static final String PORTABILITY_PROPERTIES_FILE_PATH = "src/main/resources/portability/portabilities_definitions.yml";

    @PostConstruct
    public void init() throws ParsingException {
        // load the portability properties definitions
        ParsedPropertiesDefinitions parsed = parser.parseFile(Paths.get(PORTABILITY_PROPERTIES_FILE_PATH)).getResult();
        portabilityDefinitions = keysFromStringToEnum(parsed.getDefinitions());
    }

    /**
     * For a given portability indicator, provide the right {@link PropertyDefinition}.
     */
    public PropertyDefinition getPropertyDefinition(String portabilityIndicatorKey) {
        try {
            return portabilityDefinitions.get(PortabilityPropertyEnum.valueOf(portabilityIndicatorKey));
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(String.format("The PortabilityPropertyEnum entry <%s> does not exists !", portabilityIndicatorKey));
        }
    }

    private Map<PortabilityPropertyEnum, PropertyDefinition> keysFromStringToEnum(Map<String, PropertyDefinition> definitions) {
        Map<PortabilityPropertyEnum, PropertyDefinition> toReturn = Maps.newHashMap();
        for (Entry<String, PropertyDefinition> entry : definitions.entrySet()) {
            try {
                toReturn.put(PortabilityPropertyEnum.valueOf(entry.getKey()), entry.getValue());
            } catch (IllegalArgumentException e) {
                log.warn("Unrecognized portability property <" + entry.getKey() + ">. Will be ignored");
            }
        }
        return toReturn;
    }

}
