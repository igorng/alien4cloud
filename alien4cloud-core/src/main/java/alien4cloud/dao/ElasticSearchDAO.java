package alien4cloud.dao;

import java.beans.IntrospectionException;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import alien4cloud.exception.IndexingServiceException;
import alien4cloud.model.application.Application;
import alien4cloud.model.application.ApplicationEnvironment;
import alien4cloud.model.application.ApplicationVersion;
import alien4cloud.model.application.DeploymentSetup;
import alien4cloud.model.cloud.Cloud;
import alien4cloud.model.cloud.CloudConfiguration;
import alien4cloud.model.cloud.CloudImage;
import alien4cloud.model.common.MetaPropConfiguration;
import alien4cloud.model.components.Csar;
import alien4cloud.model.components.IndexedArtifactToscaElement;
import alien4cloud.model.components.IndexedArtifactType;
import alien4cloud.model.components.IndexedCapabilityType;
import alien4cloud.model.components.IndexedNodeType;
import alien4cloud.model.components.IndexedRelationshipType;
import alien4cloud.model.components.IndexedToscaElement;
import alien4cloud.model.deployment.Deployment;
import alien4cloud.model.templates.TopologyTemplate;
import alien4cloud.model.topology.Topology;
import alien4cloud.plugin.Plugin;
import alien4cloud.plugin.PluginConfiguration;

/**
 * Elastic Search DAO for alien 4 cloud application.
 *
 * @author luc boutier
 */
@Component("alien-es-dao")
public class ElasticSearchDAO extends ESGenericSearchDAO {

    public static final String TOSCA_ELEMENT_INDEX = "toscaelement";

    /**
     * Initialize the dao after being loaded by spring (Create the indexes).
     */
    @PostConstruct
    public void initEnvironment() {
        // init ES annotation scanning
        try {
            getMappingBuilder().initialize("alien4cloud");
        } catch (IntrospectionException | IOException e) {
            throw new IndexingServiceException("Could not initialize elastic search mapping builder", e);
        }
        // init indices and mapped classes
        setJsonMapper(new ElasticSearchMapper());

        initIndices(TOSCA_ELEMENT_INDEX, null, IndexedCapabilityType.class, IndexedArtifactType.class, IndexedRelationshipType.class, IndexedNodeType.class);
        initIndices(TOSCA_ELEMENT_INDEX, null, IndexedArtifactToscaElement.class, IndexedToscaElement.class);

        initIndice(Application.class);
        initIndice(ApplicationVersion.class);
        initIndice(ApplicationEnvironment.class);
        initIndice(DeploymentSetup.class);
        initIndice(Topology.class);
        initIndice(Csar.class);
        initIndice(Plugin.class);
        initIndice(PluginConfiguration.class);
        initIndice(TopologyTemplate.class);
        initIndice(MetaPropConfiguration.class);
        initIndice(Cloud.class);
        initIndice(CloudConfiguration.class);
        initIndice(Deployment.class);
        initIndice(CloudImage.class);
        initCompleted();
    }

    private void initIndice(Class<?> clazz) {
        initIndices(clazz.getSimpleName().toLowerCase(), null, clazz);
    }
}
