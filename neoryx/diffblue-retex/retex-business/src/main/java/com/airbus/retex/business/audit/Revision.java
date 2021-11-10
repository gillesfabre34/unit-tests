package com.airbus.retex.business.audit;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;

/***
 * Class Revision for hibernate envers
 *
 * @author mduretti
 *
 */
@Getter
@Setter
@Entity
@Table(name = "revision")
@RevisionEntity(RevisionListenerImpl.class)
public class Revision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private int id;

    @RevisionTimestamp
    private long timestamp;

    private Long userId;
}
