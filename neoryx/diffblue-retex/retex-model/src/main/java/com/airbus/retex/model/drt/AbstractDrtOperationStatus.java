package com.airbus.retex.model.drt;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.common.EnumStatus;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Table(name = "drt_operation_status")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discr", discriminatorType = DiscriminatorType.STRING)
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractDrtOperationStatus extends AbstractBaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drt_id")
    protected Drt drt;

    @NonNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    protected EnumStatus status;
}
