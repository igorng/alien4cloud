package alien4cloud.it;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = { "classpath:alien/rest/portability/portability.feature" }, format = { "pretty", "html:target/cucumber/portability",
        "json:target/cucumber/cucumber-portability.json" })
public class RunPortabilityIT {
}
