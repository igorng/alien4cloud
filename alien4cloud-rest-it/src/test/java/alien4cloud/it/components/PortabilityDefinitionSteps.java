package alien4cloud.it.components;

import static org.junit.Assert.assertNotNull;

import alien4cloud.it.Context;
import alien4cloud.model.components.IndexedNodeType;
import alien4cloud.model.components.ListPropertyValue;
import alien4cloud.model.components.portability.NodeTypePortability;
import alien4cloud.model.components.portability.PortabilityProperty;
import alien4cloud.rest.model.RestResponse;
import alien4cloud.rest.utils.JsonUtil;
import alien4cloud.utils.ReflectionUtil;
import com.google.common.base.Splitter;
import cucumber.api.java.en.Then;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Assert;

public class PortabilityDefinitionSteps {
    @Then("^the node type component should have tho folowing portability informations:$")
    public void the_node_type_component_should_have_tho_folowing_portability_informations(Map<String, String> portabilityInfos) throws Throwable {
        RestResponse<IndexedNodeType> response = JsonUtil.read(Context.getInstance().getRestResponse(), IndexedNodeType.class, Context.getJsonMapper());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getPortability());
        NodeTypePortability portability = (NodeTypePortability) response.getData().getPortability();

        for (Entry<String, String> entry : portabilityInfos.entrySet()) {
            List<String> expectedValues = Splitter.on(",").splitToList(entry.getValue());
            PortabilityProperty portabilityProperty = (PortabilityProperty) ReflectionUtil.getPropertyValue(portability, entry.getKey());
            assertNotNull(portabilityProperty);
            ListPropertyValue propertyValue = (ListPropertyValue) portabilityProperty.getValue();
            assertNotNull(propertyValue);
            List<Object> currentValues = propertyValue.getValue();
            String[] expectedValuesArray = expectedValues.toArray(new String[] {});
            String[] currenValuesArray = currentValues.toArray(new String[] {});
            Arrays.sort(expectedValuesArray);
            Arrays.sort(currenValuesArray);
            Assert.assertArrayEquals(expectedValuesArray, currenValuesArray);
        }
    }
}
