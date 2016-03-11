package alien4cloud.common.parser;

import alien4cloud.common.ParsedPropertiesDefinitions;
import alien4cloud.tosca.parser.INodeParser;
import alien4cloud.tosca.parser.ParsingContextExecution;
import alien4cloud.tosca.parser.ParsingException;
import alien4cloud.tosca.parser.YamlParser;
import alien4cloud.tosca.parser.mapping.generator.MappingGenerator;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.nodes.Node;

/**
 * Parse a list of properties definition from a yaml format.
 */
@Component
public class PropertiesDefinitionYamlParser extends YamlParser<ParsedPropertiesDefinitions> {
    private static final String PROPERTIES_TYPE = "properties";

    @Inject
    private MappingGenerator mappingGenerator;

    private Map<String, INodeParser> parsers;

    @PostConstruct
    public void initialize() throws ParsingException {
        parsers = mappingGenerator.process("classpath:properties-definitions-dsl.yml");
    }

    @Override
    protected INodeParser<ParsedPropertiesDefinitions> getParser(Node rootNode, ParsingContextExecution context) throws ParsingException {
        context.setRegistry(parsers);
        return parsers.get(PROPERTIES_TYPE);
    }
}