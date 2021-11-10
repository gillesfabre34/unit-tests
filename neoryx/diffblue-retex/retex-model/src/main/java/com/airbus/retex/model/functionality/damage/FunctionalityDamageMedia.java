package com.airbus.retex.model.functionality.damage;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.media.Media;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FunctionalityDamageMedia extends AbstractBaseModel {

    private static final long serialVersionUID = 1344377706277625447L;

    @Column(name = "functionality_damage_id")
    private Long functionalityDamageId;

    @Column(name = "media_id")
    @Type(type = "uuid-char")
    private UUID mediaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", referencedColumnName = "uuid", insertable = false, updatable = false)
    private Media media;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functionality_damage_id", referencedColumnName = "id", insertable = false, updatable = false)
    private FunctionalityDamage functionalityDamage;

}
