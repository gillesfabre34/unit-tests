package com.airbus.retex.model.media;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@MappedSuperclass
@Audited
@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractMedia implements IBaseMedia {

    @Column(name = "file_name", nullable = false)
    protected String filename;

    @Column(name = "created_at")
    @CreationTimestamp
    protected LocalDateTime createdAt;

    @Column(name = "is_from_thingworx", nullable = false)
    protected Boolean isFromThingworx;

    /**
     * @param filename
     */
    public AbstractMedia(String filename) {
        if (filename.indexOf('.') == -1) {
            throw new IllegalArgumentException(String.format("'%s' does not have a file extension", filename));
        }
        this.filename = filename;
        this.createdAt = LocalDateTime.now();
        this.isFromThingworx = Boolean.FALSE;
    }

    /**
     * @param filename
     * @param isFromThingworx
     */
    public AbstractMedia(String filename, Boolean isFromThingworx) {
        if (filename.indexOf('.') == -1) {
            throw new IllegalArgumentException(String.format("'%s' does not have a file extension", filename));
        }
        this.filename = filename;
        this.createdAt = LocalDateTime.now();
        this.isFromThingworx = isFromThingworx;
    }
}
