package alien4cloud.common;

import alien4cloud.model.components.PropertyDefinition;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.common.collect.Maps;

@Getter
@Setter
public class ParsedPropertiesDefinitions {
    private Map<String, PropertyDefinition> definitions = Maps.newHashMap();
}
