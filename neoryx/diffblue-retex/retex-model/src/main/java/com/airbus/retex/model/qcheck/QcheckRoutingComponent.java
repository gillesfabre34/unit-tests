package com.airbus.retex.model.qcheck;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QcheckRoutingComponent extends AbstractBaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routing_component_index_id")
    private RoutingComponentIndex routingComponentIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drt_id")
    private Drt drt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_functional_area_id")
    private OperationFunctionalArea operationFunctionalArea;

    @Nullable
    @Column(name = "qcheck_value")
    private Boolean value;
}
