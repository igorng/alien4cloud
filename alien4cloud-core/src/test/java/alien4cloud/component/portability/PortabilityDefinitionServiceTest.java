package alien4cloud.component.portability;

import alien4cloud.exception.NotFoundException;
import alien4cloud.model.components.PropertyDefinition;
import alien4cloud.tosca.parser.ParsingException;
import java.io.FileNotFoundException;
import java.util.Map;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:portability/portability-application-context-test.xml")
public class PortabilityDefinitionServiceTest {
    @Inject
    private PortabilityDefinitionService portabilityDefinitionService;

    @Test
    public void testParsing() throws FileNotFoundException, ParsingException {
        Assert.assertNotNull(portabilityDefinitionService.getPortabilityDefinitions());
        Map<PortabilityPropertyEnum, PropertyDefinition> properties = portabilityDefinitionService.getPortabilityDefinitions();
        Assert.assertEquals(PortabilityPropertyEnum.values().length, properties.size());
    }

    @Test
    public void testGetPropertyDefinition() {
        PropertyDefinition iaassDef = portabilityDefinitionService.getPropertyDefinition("IAASS");
        Assert.assertNotNull(iaassDef);
        Assert.assertEquals("list", iaassDef.getType());
    }

    @Test(expected = NotFoundException.class)
    public void testGetPropertyDefinitionFailWithUnknownKey() {
        portabilityDefinitionService.getPropertyDefinition("Hobbit");
    }
}