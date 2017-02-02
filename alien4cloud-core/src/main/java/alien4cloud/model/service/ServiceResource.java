package alien4cloud.model.service;

import alien4cloud.model.common.IDatableResource;
import alien4cloud.paas.plan.ToscaNodeLifecycleConstants;
import alien4cloud.security.AbstractSecurityEnabledResource;
import alien4cloud.utils.version.Version;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.alien4cloud.tosca.model.instances.NodeInstance;
import org.elasticsearch.annotation.ESObject;
import org.elasticsearch.annotation.Id;
import org.elasticsearch.annotation.ObjectField;
import org.elasticsearch.annotation.StringField;
import org.elasticsearch.annotation.query.TermFilter;
import org.elasticsearch.mapping.IndexType;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;

/**
 * A service is something running somewhere, exposing capabilities and requirements, matchable in a topology in place of an abstract component.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@ESObject
@ApiModel(value = "Service.", description = "A service is something running somewhere, exposing capabilities and requirements, matchable in a topology in place of an abstract component.")
public class ServiceResource extends AbstractSecurityEnabledResource implements IDatableResource {

    @Id
    private String id;

    @NotBlank
    @TermFilter
    @StringField(indexType = IndexType.not_analyzed)
    private String name;

    @TermFilter
    @StringField(indexType = IndexType.not_analyzed)
    private String version;

    @ObjectField
    @TermFilter(paths = { "majorVersion", "minorVersion", "incrementalVersion", "buildNumber", "qualifier" })
    private Version nestedVersion;

    @StringField(indexType = IndexType.analyzed)
    private String description;

    @ObjectField
    private NodeInstance nodeInstance;

    /** Id of the locations that are authorized to match this service. */
    @TermFilter
    @StringField(indexType = IndexType.not_analyzed, includeInAll = false)
    private String[] locationIds;

    /** Id of the deployment that has initialized this service (when the service is created from a deployment). */
    @TermFilter
    @StringField(indexType = IndexType.not_analyzed, includeInAll = false)
    private String deploymentId;

    private Date creationDate;

    // The last date the state has changed.
    private Date lastUpdateDate;

    public void start() {
        nodeInstance.setAttribute(ToscaNodeLifecycleConstants.ATT_STATE, ToscaNodeLifecycleConstants.STARTED);
    }

    public void stop() {
        nodeInstance.setAttribute(ToscaNodeLifecycleConstants.ATT_STATE, ToscaNodeLifecycleConstants.STOPPED);
    }
}
