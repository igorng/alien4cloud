package alien4cloud.plugin.mock;

import alien4cloud.orchestrators.plugin.model.PluginArchive;
import alien4cloud.plugin.model.ManagedPlugin;
import alien4cloud.tosca.ArchiveParser;
import alien4cloud.tosca.model.ArchiveRoot;
import alien4cloud.tosca.parser.ParsingException;
import alien4cloud.tosca.parser.ParsingResult;
import com.google.common.collect.Lists;
import java.nio.file.Path;
import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class MockArchiveParser {

    @Inject
    private ManagedPlugin selfContext;

    @Inject
    private ArchiveParser archiveParser;

    List<PluginArchive> parseArchives(String... paths) throws ParsingException {
        List<PluginArchive> archives = Lists.newArrayList();
        for (String path : paths) {
            addToAchive(archives, path);
        }
        return archives;
    }

    private void addToAchive(List<PluginArchive> archives, String path) throws ParsingException {
        Path archivePath = selfContext.getPluginPath().resolve(path);
        // Parse the archives
        ParsingResult<ArchiveRoot> result = archiveParser.parseDir(archivePath);
        PluginArchive pluginArchive = new PluginArchive(result.getResult(), archivePath);
        archives.add(pluginArchive);
    }

}
