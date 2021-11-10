package com.airbus.retex.model.basic;


import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Audited
@Getter
@Setter
public abstract class AbstractVersionableModel<T> extends AbstractVersionableChildModel<T> {

    @Column(name = "maj_date")
    @UpdateTimestamp
    protected LocalDateTime dateUpdate;

    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "version")
    protected Long versionNumber;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EnumStatus status;

    @Column(name = "is_latest_version")
    protected Boolean isLatestVersion;
}
