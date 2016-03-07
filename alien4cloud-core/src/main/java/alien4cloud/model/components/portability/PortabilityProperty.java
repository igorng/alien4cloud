package alien4cloud.model.components.portability;

import alien4cloud.json.deserializer.PropertyValueDeserializer;
import alien4cloud.model.components.AbstractPropertyValue;
import alien4cloud.utils.jackson.ConditionalAttributes;
import alien4cloud.utils.jackson.ConditionalOnAttribute;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/***
 * A porability property. Contains its definition and value
 * 
 * @author igor
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class PortabilityProperty {
    private PortabilityPropertyDefinition definition;

    @ConditionalOnAttribute(ConditionalAttributes.REST)
    @JsonDeserialize(using = PropertyValueDeserializer.class)
    private AbstractPropertyValue value;
}
