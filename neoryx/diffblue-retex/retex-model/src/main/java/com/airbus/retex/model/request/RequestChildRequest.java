package com.airbus.retex.model.request;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.childrequest.ChildRequest;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Getter
@Setter
@Table(name = "request_child_request")
public class RequestChildRequest extends AbstractBaseModel {
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", insertable = false, updatable = false)
    private Request request;

    @Column(name = "child_request_id")
    private Long childRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_request_id", insertable = false, updatable = false)
    private ChildRequest childRequest;
}
