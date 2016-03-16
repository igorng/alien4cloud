package alien4cloud.it.components;

import static org.junit.Assert.assertNotNull;

import alien4cloud.it.Context;
import alien4cloud.model.components.AbstractPropertyValue;
import alien4cloud.model.components.IndexedNodeType;
import alien4cloud.model.components.ListPropertyValue;
import alien4cloud.rest.model.RestResponse;
import alien4cloud.rest.utils.JsonUtil;
import com.google.common.base.Splitter;
import cucumber.api.java.en.Then;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Assert;

public class PortabilityDefinitionSteps {
    @Then("^the node type component should have tho folowing portability informations:$")
    public void the_node_type_component_should_have_tho_folowing_portability_informations(Map<String, String> expectedPortabilityInfos) throws Throwable {
        System.out.println(Context.getInstance().getRestResponse());
        RestResponse<IndexedNodeType> response = JsonUtil.read(Context.getInstance().getRestResponse(), IndexedNodeType.class, Context.getJsonMapper());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getPortability());
        Map<String, AbstractPropertyValue> portability = response.getData().getPortability();

        for (Entry<String, String> entry : expectedPortabilityInfos.entrySet()) {
            String key = entry.getKey().toUpperCase();
            List<String> expectedValues = Splitter.on(",").splitToList(entry.getValue());
            ListPropertyValue portabilityValue = (ListPropertyValue) portability.get(key);
            assertNotNull(portabilityValue);
            List<Object> currentValues = portabilityValue.getValue();
            String[] expectedValuesArray = expectedValues.toArray(new String[] {});
            String[] currenValuesArray = currentValues.toArray(new String[] {});
            Arrays.sort(expectedValuesArray);
            Arrays.sort(currenValuesArray);
            Assert.assertArrayEquals(expectedValuesArray, currenValuesArray);
        }
    }
}
