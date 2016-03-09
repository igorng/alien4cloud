package alien4cloud.plugin.mock;

import alien4cloud.orchestrators.plugin.ILocationConfiguratorPlugin;
import alien4cloud.orchestrators.plugin.model.PluginArchive;
import alien4cloud.paas.exception.PluginParseException;
import alien4cloud.tosca.parser.ParsingException;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Mock implementation for an orchestrator instance.
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class MockOrchestrator extends MockPaaSProvider {
    @Inject
    private MockLocationConfigurerFactory mockLocationConfigurerFactory;
    @Inject
    private MockArchiveParser mockArchiveParser;

    private List<PluginArchive> archives;

    @Override
    public ILocationConfiguratorPlugin getConfigurator(String locationType) {
        return mockLocationConfigurerFactory.newInstance(locationType);
    }

    @Override
    public List<PluginArchive> pluginArchives() {
        if (archives == null) {
            try {
                archives = mockArchiveParser.parseArchives("common");
            } catch (ParsingException e) {
                log.error(e.getMessage());
                throw new PluginParseException(e.getMessage());
            }
        }
        return archives;
    }
}