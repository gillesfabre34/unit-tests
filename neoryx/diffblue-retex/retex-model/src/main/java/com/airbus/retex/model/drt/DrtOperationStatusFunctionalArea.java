package com.airbus.retex.model.drt;

import com.airbus.retex.model.operation.OperationFunctionalArea;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@DiscriminatorValue("DRT_OFA")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrtOperationStatusFunctionalArea extends AbstractDrtOperationStatus {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ope_functional_area_id")
    private OperationFunctionalArea operationFunctionalArea;

}
