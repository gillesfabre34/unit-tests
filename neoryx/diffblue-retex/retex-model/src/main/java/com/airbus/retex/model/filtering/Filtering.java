package com.airbus.retex.model.filtering;

import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumFilteringPosition;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.media.Media;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


/**
 * The persistent class for the Filtering table.
 */

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Filtering extends AbstractBaseModel {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drt_id")
    private Drt drt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "physical_part_id")
    private PhysicalPart physicalPart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_family_id")
    private AircraftFamily aircraftFamily;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_type_id")
    private AircraftType aircraftType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_version_id")
    private AircraftVersion aircraftVersion;

    @Column(name = "aircraft_serial_number")
    private String aircraftSerialNumber;

    @Column(name = "notification")
    private String notification;

    @Column(name = "position")
    @Enumerated(EnumType.STRING)
    private EnumFilteringPosition position;

    @NonNull
    @Column(name = "filtering_date")
    @CreationTimestamp
    private LocalDate filteringDate;

    @Column(name = "last_modified")
    private LocalDate lastModified;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EnumStatus status;

    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "filtering_media",
            joinColumns = {@JoinColumn(name = "filtering_id")},
            inverseJoinColumns = {@JoinColumn(name = "media_id")}
    )
    private Set<Media> medias = new HashSet<>();

    /**
     * Adds a media.
     *
     * @param media a Media
     */
    public void addMedia(final Media media) {
        if (media != null) {
            getMedias().add(media);
        }
    }

}