package com.airbus.retex.model.media;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
public class MediaTmp extends AbstractMedia implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uuid", columnDefinition = "BINARY(16)")
    @Type(type = "uuid-char")
    private UUID uuid;

    /**
     * @param filename
     */
    public MediaTmp(String filename) {
        super(filename);
    }

    /**
     * @param filename
     * @param isFromThingworx
     */
    public MediaTmp(String filename, Boolean isFromThingworx) {
        super(filename, isFromThingworx);
    }

    @Override
    public UUID getId() {
        return getUuid();
    }
}
