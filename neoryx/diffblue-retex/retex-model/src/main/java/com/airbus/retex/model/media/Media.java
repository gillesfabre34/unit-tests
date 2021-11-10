package com.airbus.retex.model.media;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Media extends AbstractMedia implements Serializable {

    public static final String MEDIA_CACHE = "mediaCacheable";

    @Id
    @Column(name = "uuid", columnDefinition = "BINARY(16)")
    @Type(type = "uuid-char")
    private UUID uuid;

    /**
     * @param uuid
     * @param filename
     */
    public Media(UUID uuid, String filename) {
        super(filename);
        this.uuid = uuid;
    }

    /**
     * @param filename
     */
    public Media(String filename) {
        super(filename);
        this.uuid = generateUuid();
    }

    /**
     * @param filename
     * @param isFromThingworx
     */
    public Media(String filename, Boolean isFromThingworx) {
        super(filename, isFromThingworx);
        this.uuid = generateUuid();
    }

    /**
     * @param tempMedia
     */
    public Media(MediaTmp tempMedia) {
        this.uuid = tempMedia.getUuid();
        this.filename = tempMedia.getFilename();
        this.createdAt = LocalDateTime.now();
        this.isFromThingworx = tempMedia.getIsFromThingworx();
    }

    @Override
    public UUID getId() {
        return getUuid();
    }

    private UUID generateUuid() {
        return UUID.randomUUID();
    }
}
