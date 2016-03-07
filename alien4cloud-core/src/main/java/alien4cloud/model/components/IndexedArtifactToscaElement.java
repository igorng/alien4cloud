package alien4cloud.model.components;

import static alien4cloud.dao.model.FetchContext.QUICK_SEARCH;
import static alien4cloud.dao.model.FetchContext.SUMMARY;
import static alien4cloud.dao.model.FetchContext.TAG_SUGGESTION;

import alien4cloud.json.deserializer.AttributeDeserializer;
import alien4cloud.json.deserializer.PortabilityDeserializer;
import alien4cloud.model.components.portability.AbstractPortability;
import alien4cloud.utils.jackson.ConditionalAttributes;
import alien4cloud.utils.jackson.ConditionalOnAttribute;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.annotation.query.FetchContext;

@Getter
@Setter
@SuppressWarnings("PMD.UnusedPrivateField")
@JsonInclude(Include.NON_NULL)
public class IndexedArtifactToscaElement extends IndexedInheritableToscaElement {
    @FetchContext(contexts = { SUMMARY, QUICK_SEARCH, TAG_SUGGESTION }, include = { false, false, false })
    private Map<String, DeploymentArtifact> artifacts;

    @FetchContext(contexts = { SUMMARY, QUICK_SEARCH, TAG_SUGGESTION }, include = { false, false, false })
    @JsonDeserialize(contentUsing = AttributeDeserializer.class)
    private Map<String, IValue> attributes;

    @FetchContext(contexts = { SUMMARY, QUICK_SEARCH, TAG_SUGGESTION }, include = { false, false, false })
    private Map<String, Interface> interfaces;

    @ConditionalOnAttribute(ConditionalAttributes.REST)
    @JsonDeserialize(using = PortabilityDeserializer.class)
    private AbstractPortability portability;

}