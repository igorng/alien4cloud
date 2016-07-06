package alien4cloud.tosca;

import alien4cloud.component.repository.exception.CSARVersionAlreadyExistsException;
import alien4cloud.model.components.CSARDependency;
import alien4cloud.model.components.CSARSource;
import alien4cloud.model.components.Csar;
import alien4cloud.model.git.CsarDependenciesBean;
import alien4cloud.security.AuthorizationUtil;
import alien4cloud.security.model.Role;
import alien4cloud.suggestions.services.SuggestionService;
import alien4cloud.topology.TopologySynchService;
import alien4cloud.topology.TopologyTemplateVersionService;
import alien4cloud.tosca.context.ToscaContextual;
import alien4cloud.tosca.model.ArchiveRoot;
import alien4cloud.tosca.parser.ParsingContext;
import alien4cloud.tosca.parser.ParsingErrorLevel;
import alien4cloud.tosca.parser.ParsingException;
import alien4cloud.tosca.parser.ParsingResult;
import com.google.common.collect.Maps;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ArchiveUploadService {

    @Inject
    private ArchiveParser parser;
    @Inject
    private ArchiveIndexer archiveIndexer;
    @Inject
    TopologySynchService topologySynchService;
    @Inject
    TopologyTemplateVersionService topologyTemplateVersionService;
    @Inject
    private SuggestionService suggestionService;

    /**
     * Upload a TOSCA archive and index its components.
     * 
     * @param path The archive path.
     * @param csarSource The source of the upload.
     * @return The Csar object from the parsing.
     * @throws ParsingException
     * @throws CSARVersionAlreadyExistsException
     */
    @ToscaContextual
    public ParsingResult<Csar> upload(Path path, CSARSource csarSource) throws ParsingException, CSARVersionAlreadyExistsException {
        // parse the archive.
        ParsingResult<ArchiveRoot> parsingResult = parser.parse(path);

        final ArchiveRoot archiveRoot = parsingResult.getResult();
        if (archiveRoot.hasToscaTopologyTemplate()) {
            AuthorizationUtil.checkHasOneRoleIn(Role.ARCHITECT, Role.ADMIN);
        }
        if (archiveRoot.hasToscaTypes()) {
            AuthorizationUtil.checkHasOneRoleIn(Role.COMPONENTS_MANAGER, Role.ADMIN);
        }

        // check if any blocker error has been found during parsing process.
        if (parsingResult.hasError(ParsingErrorLevel.ERROR)) {
            // do not save anything if any blocker error has been found during import.
            return toSimpleResult(parsingResult);
        }

        archiveIndexer.importArchive(archiveRoot, csarSource, path, parsingResult.getContext().getParsingErrors());
        try {
            suggestionService.postProcessSuggestionFromArchive(parsingResult);
            suggestionService.setAllSuggestionIdOnPropertyDefinition();
        } catch (Exception e) {
            log.error("Could not post process suggestion for the archive", e);
        }

        // resynch topologies if needed
        // String embededTopologyId = parsingResult.getResult().getTopology() != null ? parsingResult.getResult().getTopology().getId() : null;
        // topologySynchService.synchTopologiesWith(parsingResult.getResult().getArchive(), embededTopologyId);

        return toSimpleResult(parsingResult);
    }

    @ToscaContextual
    public Map<CSARDependency, CsarDependenciesBean> preParsing(Set<Path> paths) throws ParsingException {
        Map<CSARDependency, CsarDependenciesBean> csarDependenciesBeans = Maps.newHashMap();
        for (Path path : paths) {
            try {
                ParsingResult<ArchiveRoot> parsingResult = parser.parse(path);
                CsarDependenciesBean csarDepContainer = new CsarDependenciesBean();
                csarDepContainer.setPath(path);
                csarDepContainer
                        .setSelf(new CSARDependency(parsingResult.getResult().getArchive().getName(), parsingResult.getResult().getArchive().getVersion()));
                csarDepContainer.setDependencies(parsingResult.getResult().getArchive().getDependencies());
                csarDependenciesBeans.put(csarDepContainer.getSelf(), csarDepContainer);
            } catch (Exception e) {
                // TODO: error should be returned in a way or another
                log.debug("Not able to parse archive, ignoring it", e);
            }
        }
        return csarDependenciesBeans;
    }

    /**
     * Create a simple result without all the parsed data but just the {@link Csar} object as well as the eventual errors.
     * 
     * @return The simple result out of the complex parsing result.
     */
    private ParsingResult<Csar> toSimpleResult(ParsingResult<ArchiveRoot> parsingResult) {
        ParsingResult<Csar> simpleResult = this.<Csar> cleanup(parsingResult);
        simpleResult.setResult(parsingResult.getResult().getArchive());
        return simpleResult;
    }

    private <T> ParsingResult<T> cleanup(ParsingResult<?> result) {
        ParsingContext context = new ParsingContext(result.getContext().getFileName());
        context.getParsingErrors().addAll(result.getContext().getParsingErrors());
        ParsingResult<T> cleaned = new ParsingResult<T>(null, context);
        return cleaned;
    }
}