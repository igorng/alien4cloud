package alien4cloud.topology.validation;

import alien4cloud.component.CSARRepositorySearchService;
import alien4cloud.component.IToscaElementFinder;
import alien4cloud.exception.NotFoundException;
import alien4cloud.model.components.CSARDependency;
import alien4cloud.model.components.CapabilityDefinition;
import alien4cloud.model.components.IndexedNodeType;
import alien4cloud.model.components.IndexedToscaElement;
import alien4cloud.model.topology.Capability;
import alien4cloud.model.topology.NodeTemplate;
import alien4cloud.model.topology.RelationshipTemplate;
import alien4cloud.topology.TopologyServiceCore;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class TopologyCapabilityBoundsValidationServices {
    @Resource
    private CSARRepositorySearchService csarRepoSearchService;
    @Resource
    private TopologyServiceCore topologyServiceCore;

    //
    public boolean isCapabilityUpperBoundReachedForTarget(String nodeTemplateName, Map<String, NodeTemplate> nodeTemplates, String capabilityName,
            final Set<CSARDependency> dependencies) {
        return isCapabilityUpperBoundReachedForTarget(nodeTemplateName, nodeTemplates, capabilityName, new IToscaElementFinder() {

            @Override
            public <T extends IndexedToscaElement> T findElement(Class<T> elementClass, String elementId) {
                return csarRepoSearchService.getRequiredElementInDependencies(elementClass, elementId, dependencies);
            }
        });
    }

    public boolean isCapabilityUpperBoundReachedForTarget(String nodeTemplateName, Map<String, NodeTemplate> nodeTemplates, String capabilityName,
            IToscaElementFinder finder) {
        NodeTemplate nodeTemplate = nodeTemplates.get(nodeTemplateName);
        chekCapability(nodeTemplateName, capabilityName, nodeTemplate);

        IndexedNodeType relatedIndexedNodeType = finder.findElement(IndexedNodeType.class, nodeTemplate.getType());
        CapabilityDefinition capabilityDefinition = getCapabilityDefinition(relatedIndexedNodeType.getCapabilities(), capabilityName);
        if (capabilityDefinition.getUpperBound() == Integer.MAX_VALUE) {
            return false;
        }

        List<RelationshipTemplate> targetRelatedRelationships = topologyServiceCore.getTargetRelatedRelatonshipsTemplate(nodeTemplateName, nodeTemplates);
        if (targetRelatedRelationships == null || targetRelatedRelationships.isEmpty()) {
            return false;
        }

        int count = 0;
        for (RelationshipTemplate rel : targetRelatedRelationships) {
            if (rel.getTargetedCapabilityName().equals(capabilityName)) {
                count++;
            }
        }

        return count >= capabilityDefinition.getUpperBound();
    }

    private CapabilityDefinition getCapabilityDefinition(Collection<CapabilityDefinition> capabilityDefinitions, String capabilityName) {
        for (CapabilityDefinition capabilityDef : capabilityDefinitions) {
            if (capabilityDef.getId().equals(capabilityName)) {
                return capabilityDef;
            }
        }

        throw new NotFoundException("Capability definition [" + capabilityName + "] cannot be found");
    }

    private void chekCapability(String nodeTemplateName, String capabilityName, NodeTemplate nodeTemplate) {
        boolean capablityExists = false;
        if (nodeTemplate.getCapabilities() != null) {
            for (Map.Entry<String, Capability> capaEntry : nodeTemplate.getCapabilities().entrySet()) {
                if (capaEntry.getKey().equals(capabilityName)) {
                    capablityExists = true;
                }
            }
        }
        if (!capablityExists) {
            throw new NotFoundException("A capability with name [" + capabilityName + "] cannot be found in the target node [" + nodeTemplateName + "].");
        }
    }
}