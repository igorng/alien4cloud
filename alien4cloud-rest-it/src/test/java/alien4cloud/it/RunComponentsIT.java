package alien4cloud.it;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = { "classpath:alien/rest/components"
        // "classpath:alien/rest/components/search_components_enhanced.feature"
        // "classpath:alien/rest/components/search_components.feature"
        // "classpath:alien/rest/components/portability.feature"
}, format = { "pretty", "html:target/cucumber/components", "json:target/cucumber/cucumber-components.json" })
public class RunComponentsIT {
}
