package alien4cloud.json.deserializer;

import alien4cloud.model.components.AbstractPropertyValue;
import alien4cloud.model.components.ComplexPropertyValue;
import alien4cloud.model.components.FunctionPropertyValue;
import alien4cloud.model.components.ListPropertyValue;
import alien4cloud.model.components.ScalarPropertyValue;
import com.fasterxml.jackson.databind.node.JsonNodeType;

/**
 * Custom deserializer to handle multiple IOperationParameter types.
 */
public class AllPropertyValueDeserializer extends AbstractDiscriminatorPolymorphicDeserializer<AbstractPropertyValue> {
    private static final long serialVersionUID = -3273872739983840681L;

    public AllPropertyValueDeserializer() {
        super(AbstractPropertyValue.class);
        addToRegistry("function", FunctionPropertyValue.class);
        addToRegistry("value", JsonNodeType.STRING.toString(), ScalarPropertyValue.class);
        addToRegistry("value", JsonNodeType.ARRAY.toString(), ListPropertyValue.class);
        addToRegistry("value", JsonNodeType.OBJECT.toString(), ComplexPropertyValue.class);
        setValueStringClass(ScalarPropertyValue.class);
    }
}