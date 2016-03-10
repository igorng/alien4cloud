package alien4cloud.json.deserializer;

/**
 * Custom deserializer to handle multiple IOperationParameter types.
 */
public class PropertyValueDeserializer extends AllPropertyValueDeserializer {
    private static final long serialVersionUID = -1700625129118450029L;

    public PropertyValueDeserializer() {
        removeFromRegistry("function");
    }
}