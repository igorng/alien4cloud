package alien4cloud.common.parser;

import alien4cloud.common.ParsedPropertiesDefinitions;
import alien4cloud.model.components.PropertyDefinition;
import alien4cloud.model.components.constraints.ValidValuesConstraint;
import alien4cloud.tosca.parser.ParsingException;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Map;
import javax.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:matching/parser-application-context.xml")
public class PropertiesDefinitionYamlParserTest {
    @Inject
    private PropertiesDefinitionYamlParser parser;

    @Test
    public void testParsing() throws FileNotFoundException, ParsingException {
        ParsedPropertiesDefinitions parsedProperties = parser.parseFile(Paths.get("src/test/resources/alien/parsing/properties_definitions_test.yml"))
                .getResult();
        Assert.assertNotNull(parsedProperties);
        Map<String, PropertyDefinition> properties = parsedProperties.getDefinitions();
        Assert.assertTrue(MapUtils.isNotEmpty(properties));
        Assert.assertEquals(3, properties.size());
        PropertyDefinition def = properties.get("first");
        Assert.assertNotNull(def);
        Assert.assertEquals("default_first", def.getDefault());
        Assert.assertTrue(CollectionUtils.isNotEmpty(def.getConstraints()));
        Assert.assertTrue(def.getConstraints().get(0) instanceof ValidValuesConstraint);

        def = properties.get("second");
        Assert.assertNotNull(def);
        Assert.assertEquals("list", def.getType());

        def = properties.get("third");
        Assert.assertNotNull(def);
        Assert.assertEquals("map", def.getType());
    }
}