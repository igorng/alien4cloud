package alien4cloud.model.components.portability;

import alien4cloud.model.components.PropertyDefinition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Portability's property definition
 * 
 * @author igor
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class PortabilityPropertyDefinition extends PropertyDefinition {
    /**
     * Whether the property backed by this definition is editable
     */
    boolean editable;
}
