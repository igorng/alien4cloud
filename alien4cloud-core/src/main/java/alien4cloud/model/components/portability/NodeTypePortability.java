package alien4cloud.model.components.portability;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Node type portability informations
 * 
 * @author igor
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class NodeTypePortability extends AbstractPortability {
    private PortabilityProperty iaaSs;
    private PortabilityProperty orchestrators;
}
