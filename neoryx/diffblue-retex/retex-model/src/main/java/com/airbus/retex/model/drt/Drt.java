package com.airbus.retex.model.drt;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.control.AbstractControl;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.qcheck.QcheckRoutingComponent;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


/**
 * The persistent class for the DRT table.
 */

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Drt extends AbstractBaseModel {

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "drt")
    private Filtering filtering;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "child_request_id", nullable = false)
    private ChildRequest childRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routing_id")
    private Routing routing;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "drt")
    private Set<AbstractDrtOperationStatus> operationStatus = new HashSet<>();

    // Known also as creation date
    @NonNull
    @Column(name = "integration_date")
    @CreationTimestamp
    private LocalDate integrationDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "assigned_operator_id", nullable = true)
    private User assignedOperator;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "drt")
    private Set<AbstractControl> controls = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "drt")
    private Set<DrtPictures> drtPictures = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "drt")
    private Set<QcheckRoutingComponent> qCheckRoutingComponents = new HashSet<>();

    @NonNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EnumStatus status;

    public void addControl(AbstractControl control) {
        control.setDrt(this);
        controls.add(control);
    }

    /**
     * @param operationStatus
     */
    public void addOperationStatus(AbstractDrtOperationStatus operationStatus) {
        operationStatus.setDrt(this);
        this.operationStatus.add(operationStatus);
    }

    public void addDrtPictures(DrtPictures drtPictures) {
        drtPictures.setDrt(this);
        this.drtPictures.add(drtPictures);
    }

    public void addQcheckRoutingComponent(QcheckRoutingComponent qCheckRoutingComponent) {
        qCheckRoutingComponent.setDrt(this);
        this.qCheckRoutingComponents.add(qCheckRoutingComponent);
    }
}