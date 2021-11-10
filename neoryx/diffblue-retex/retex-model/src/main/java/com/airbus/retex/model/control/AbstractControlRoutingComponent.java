package com.airbus.retex.model.control;

import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Audited
@Getter
@Setter
public abstract class AbstractControlRoutingComponent<T> extends AbstractControl<T> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routing_functional_area_post_id")
    protected RoutingFunctionalAreaPost routingFunctionalAreaPost;

    public abstract T getValue();

    public abstract void setValue(T object);

}
