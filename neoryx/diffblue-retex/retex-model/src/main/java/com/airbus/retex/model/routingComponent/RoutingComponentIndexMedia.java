package com.airbus.retex.model.routingComponent;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.media.Media;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoutingComponentIndexMedia extends AbstractBaseModel implements Serializable {
    private static final long serialVersionUID = 1344377706277625452L;

    public RoutingComponentIndexMedia(RoutingComponentIndex routingComponent, Media media) {
        this.routingComponentIndexId = routingComponent.getNaturalId();
        this.mediaId = media.getUuid();
    }

    public RoutingComponentIndexMedia(Long routingComponentIndexId, UUID mediaId) {
        this.routingComponentIndexId = routingComponentIndexId;
        this.mediaId = mediaId;
    }

    @Column(name = "media_id")
    @Type(type = "uuid-char")
    private UUID mediaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", referencedColumnName = "uuid", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Media media;

    @Column(name = "routing_component_index_id")
    private Long routingComponentIndexId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routing_component_index_id", referencedColumnName = "id", insertable = false, updatable = false)
    private RoutingComponentIndex routingComponent;
}
