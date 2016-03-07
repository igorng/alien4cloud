package alien4cloud.tosca.parser.impl.advanced;

import alien4cloud.model.components.AbstractPropertyValue;
import alien4cloud.model.components.portability.PortabilityProperty;
import alien4cloud.tosca.parser.INodeParser;
import alien4cloud.tosca.parser.ParsingContextExecution;
import alien4cloud.tosca.parser.mapping.DefaultParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.nodes.Node;

@Component
@Slf4j
public class PortabilityPropertyParser extends DefaultParser<PortabilityProperty> {

    @Override
    public PortabilityProperty parse(Node node, ParsingContextExecution context) {
        PortabilityProperty portabilityProperty = new PortabilityProperty();
        INodeParser<AbstractPropertyValue> propertyParser = context.getRegistry().get("node_template_property");
        portabilityProperty.setValue(propertyParser.parse(node, context));
        return portabilityProperty;
    }

}