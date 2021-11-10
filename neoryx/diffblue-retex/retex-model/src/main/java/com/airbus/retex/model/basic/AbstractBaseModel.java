package com.airbus.retex.model.basic;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Super class of all entities.
 *
 * @author mduretti
 */

@MappedSuperclass
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractBaseModel implements Serializable, IIdentifiedModel<Long> {
    private static final long serialVersionUID = 8260967716089299344L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

}
