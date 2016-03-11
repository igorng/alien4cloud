package alien4cloud.tosca.parser;

import alien4cloud.component.portability.PortabilityPropertyEnum;
import alien4cloud.model.components.IndexedNodeType;
import alien4cloud.model.components.ListPropertyValue;
import alien4cloud.model.components.PropertyValue;
import alien4cloud.tosca.ArchiveParserTest;
import alien4cloud.tosca.model.ArchiveRoot;
import java.nio.file.Paths;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections4.MapUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test tosca parsing for Alien DSL
 * 
 * @author igor
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:tosca/parser-application-context.xml")
public class AlienDSLParserTest {
    private static final String ALIEN_DSL_ROOT_DIRECTORY = "src/test/resources/alien/tosca/parsing/";

    @Resource
    private ToscaParser parser;

    @Test
    public void testPortabilityDefinition() throws ParsingException {
        ParsingResult<ArchiveRoot> parsingResult = parser.parseFile(Paths.get(ALIEN_DSL_ROOT_DIRECTORY, "tosca-node-type-portability.yml"));
        ArchiveParserTest.displayErrors(parsingResult);
        IndexedNodeType nodeType = parsingResult.getResult().getNodeTypes().values().iterator().next();
        Assert.assertNotNull(nodeType);
        Assert.assertTrue(MapUtils.isNotEmpty(nodeType.getPortability()));
        Map<String, PropertyValue<?>> portability = nodeType.getPortability();

        Assert.assertNotNull(portability.get(PortabilityPropertyEnum.IAASS_KEY));
        Assert.assertTrue(portability.get(PortabilityPropertyEnum.IAASS_KEY) instanceof ListPropertyValue);

        Assert.assertNotNull(portability.get(PortabilityPropertyEnum.ORCHESTRATORS_KEY));
        Assert.assertTrue(portability.get(PortabilityPropertyEnum.ORCHESTRATORS_KEY) instanceof ListPropertyValue);
    }

}
