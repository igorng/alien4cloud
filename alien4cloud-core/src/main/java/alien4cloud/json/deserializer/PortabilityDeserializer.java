package alien4cloud.json.deserializer;

import alien4cloud.model.components.portability.AbstractPortability;
import alien4cloud.model.components.portability.NodeTypePortability;

/**
 * Custom deserializer to handle multiple IOperationParameter types.
 */
public class PortabilityDeserializer extends AbstractDiscriminatorPolymorphicDeserializer<AbstractPortability> {
    /**
     * 
     */
    private static final long serialVersionUID = -8848769082708434717L;

    public PortabilityDeserializer() {
        super(AbstractPortability.class);
        addToRegistry("iaaS", NodeTypePortability.class);
    }
}