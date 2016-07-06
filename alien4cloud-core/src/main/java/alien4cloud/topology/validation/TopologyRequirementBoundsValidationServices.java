package alien4cloud.topology.validation;

import alien4cloud.component.CSARRepositorySearchService;
import alien4cloud.component.IToscaElementFinder;
import alien4cloud.exception.NotFoundException;
import alien4cloud.model.components.CSARDependency;
import alien4cloud.model.components.IndexedNodeType;
import alien4cloud.model.components.IndexedToscaElement;
import alien4cloud.model.components.RequirementDefinition;
import alien4cloud.model.topology.NodeTemplate;
import alien4cloud.model.topology.RelationshipTemplate;
import alien4cloud.model.topology.Requirement;
import alien4cloud.model.topology.Topology;
import alien4cloud.topology.task.RequirementToSatisfy;
import alien4cloud.topology.task.RequirementsTask;
import alien4cloud.topology.task.TaskCode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.elasticsearch.common.collect.Lists;
import org.springframework.stereotype.Component;

/**
 * Performs validation of the requirements and capabilities bounds.
 */
@Component
public class TopologyRequirementBoundsValidationServices {
    @Resource
    private CSARRepositorySearchService csarRepoSearchService;

    /**
     * Check if the upperBound of a requirement is reached on a node template
     *
     * @param nodeTemplate the node to check for requirement bound
     * @param requirementName the name of the requirement
     * @param dependencies the dependencies of the topology
     * @return true if requirement upper bound is reached, false otherwise
     */
    public boolean isRequirementUpperBoundReachedForSource(NodeTemplate nodeTemplate, String requirementName, final Set<CSARDependency> dependencies) {
        // IndexedNodeType relatedIndexedNodeType = csarRepoSearchService.getRequiredElementInDependencies(IndexedNodeType.class, nodeTemplate.getType(),
        // dependencies);
        return isRequirementUpperBoundReachedForSource(nodeTemplate, requirementName, new IToscaElementFinder() {

            @Override
            public <T extends IndexedToscaElement> T findElement(Class<T> elementClass, String elementId) {
                return csarRepoSearchService.getRequiredElementInDependencies(elementClass, elementId, dependencies);
            }
        });
    }

    public boolean isRequirementUpperBoundReachedForSource(NodeTemplate nodeTemplate, String requirementName, IToscaElementFinder finder) {
        chekRequirement(requirementName, nodeTemplate);

        IndexedNodeType relatedIndexedNodeType = finder.findElement(IndexedNodeType.class, nodeTemplate.getType());

        if (nodeTemplate.getRelationships() == null || nodeTemplate.getRelationships().isEmpty()) {
            return false;
        }
        Requirement requirement = nodeTemplate.getRequirements().get(requirementName);

        RequirementDefinition requirementDefinition = getRequirementDefinition(relatedIndexedNodeType.getRequirements(), requirementName,
                requirement.getType());

        if (requirementDefinition.getUpperBound() == Integer.MAX_VALUE) {
            return false;
        }

        int count = countRelationshipsForRequirement(requirementName, requirement.getType(), nodeTemplate.getRelationships());

        return count >= requirementDefinition.getUpperBound();
    }

    /**
     * Perform validation of requirements bounds/occurences for the given topology.
     * 
     * @param topology The topology to check
     * @return A list of validation errors (tasks to be done to make the topology compliant).
     */
    public List<RequirementsTask> validateRequirementsLowerBounds(Topology topology) {
        List<RequirementsTask> toReturnTaskList = Lists.newArrayList();
        Map<String, NodeTemplate> nodeTemplates = topology.getNodeTemplates();
        for (Map.Entry<String, NodeTemplate> nodeTempEntry : nodeTemplates.entrySet()) {
            NodeTemplate nodeTemp = nodeTempEntry.getValue();
            if (nodeTemp.getRequirements() == null) {
                continue;
            }
            IndexedNodeType relatedIndexedNodeType = csarRepoSearchService.getRequiredElementInDependencies(IndexedNodeType.class, nodeTemp.getType(),
                    topology.getDependencies());
            // do pass if abstract node
            if (relatedIndexedNodeType.isAbstract()) {
                continue;
            }
            RequirementsTask task = new RequirementsTask();
            task.setNodeTemplateName(nodeTempEntry.getKey());
            task.setCode(TaskCode.SATISFY_LOWER_BOUND);
            task.setComponent(relatedIndexedNodeType);
            task.setRequirementsToImplement(Lists.<RequirementToSatisfy> newArrayList());
            if (CollectionUtils.isNotEmpty(relatedIndexedNodeType.getRequirements())) {
                for (RequirementDefinition reqDef : relatedIndexedNodeType.getRequirements()) {
                    int count = countRelationshipsForRequirement(reqDef.getId(), reqDef.getType(), nodeTemp.getRelationships());
                    if (count < reqDef.getLowerBound()) {
                        task.getRequirementsToImplement().add(new RequirementToSatisfy(reqDef.getId(), reqDef.getType(), reqDef.getLowerBound() - count));
                        continue;
                    }
                }
                if (CollectionUtils.isNotEmpty(task.getRequirementsToImplement())) {
                    toReturnTaskList.add(task);
                }
            }
        }
        return toReturnTaskList.isEmpty() ? null : toReturnTaskList;
    }

    /**
     * Get the number of relationships from a node template that are actually linked to the given requirement.
     * 
     * @param requirementName Name of the requirement for which to count relationships
     * @param requirementType Type of the requirement for which to count relationships
     * @param relationships Relationships connected to the node that holds the requirement.
     * @return The number of relationships connected to the given requirement.
     */
    private int countRelationshipsForRequirement(String requirementName, String requirementType, Map<String, RelationshipTemplate> relationships) {
        int count = 0;
        if (relationships == null) {
            return 0;
        }
        for (Map.Entry<String, RelationshipTemplate> relEntry : relationships.entrySet()) {
            if (relEntry.getValue().getRequirementName().equals(requirementName) && relEntry.getValue().getRequirementType().equals(requirementType)) {
                count++;
            }
        }
        return count;
    }

    private RequirementDefinition getRequirementDefinition(Collection<RequirementDefinition> requirementDefinitions, String requirementName,
            String requirementType) {
        for (RequirementDefinition requirementDef : requirementDefinitions) {
            if (requirementDef.getId().equals(requirementName) && requirementDef.getType().equals(requirementType)) {
                return requirementDef;
            }
        }
        throw new NotFoundException("Requirement definition [" + requirementName + ":" + requirementType + "] cannot be found");
    }

    private void chekRequirement(String requirementName, NodeTemplate nodeTemplate) {
        if (MapUtils.isEmpty(nodeTemplate.getRequirements()) || !nodeTemplate.getRequirements().containsKey(requirementName)) {
            throw new NotFoundException(
                    "A requirement with name [" + requirementName + "] cannot be found in the target node [" + nodeTemplate.getName() + "].");
        }
    }
}
