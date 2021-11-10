package com.airbus.retex.model.post;

import com.airbus.retex.model.basic.AbstractVersionableChildModel;
import com.airbus.retex.model.step.StepActivation;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Getter
@Setter
public class RoutingFunctionalAreaPost extends AbstractVersionableChildModel<Long> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_activation_id")
    private StepActivation stepActivation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "threshold")
    private Float threshold;
}
