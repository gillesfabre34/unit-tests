package com.airbus.retex.model.basic;

import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Super class of entities with history like Request and ChildRequest.
 *
 */

@MappedSuperclass
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractHistorizableBaseModel implements Serializable, IIdentifiedModel<Long> {
    private static final long serialVersionUID = 8270968816089299355L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "version")
    private Long version;

    // ----------------------- status ----------------------------

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EnumStatus status;

}
