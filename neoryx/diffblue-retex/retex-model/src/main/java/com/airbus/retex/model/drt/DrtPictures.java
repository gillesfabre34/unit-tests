package com.airbus.retex.model.drt;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.step.StepActivation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrtPictures extends AbstractBaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drt_id")
    private Drt drt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_activation_id")
    private StepActivation stepActivation;

    @Column(name = "activated")
    private boolean activated;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "drt_pictures_media",
            joinColumns = {@JoinColumn(name = "drt_pictures_id")},
            inverseJoinColumns = {@JoinColumn(name = "media_id")}
    )
    private Set<Media> medias = new HashSet<>();
}
