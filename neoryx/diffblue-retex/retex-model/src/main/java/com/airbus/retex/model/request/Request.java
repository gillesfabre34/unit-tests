package com.airbus.retex.model.request;

import com.airbus.retex.model.admin.airbus.AirbusEntity;
import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.model.ata.ATA;
import com.airbus.retex.model.basic.AbstractHistorizableBaseModel;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.environment.Environment;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.mission.MissionType;
import com.airbus.retex.model.origin.Origin;
import com.airbus.retex.model.translation.Translate;
import com.airbus.retex.model.user.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
public class Request  extends AbstractHistorizableBaseModel {

    public static final String FIELD_NAME = "name";

    // private Long id; has been removed / Check the filter requestName ASC/ DESC  TODO
    @Column(name = "reference")
    private String reference;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_id")
    private Origin origin;

    @Column(name = "due_date")
    private LocalDate dueDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airbus_entity_id")
    private AirbusEntity airbusEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ata_code")
    private ATA ata;

    @OneToMany(mappedBy = "entityId", fetch = FetchType.LAZY)
    @Where(clause = "class_name = 'Request'")
    private List<Translate> translates  = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validator_id")
    private User validator;


    @Column(name="origin_comment")
    private String originComment;

    @Column(name="origin_url")
    private String originUrl;

    /*    @JoinColumn(name = "origin_media_id", unique = true)
        @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)*/
    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "request_origin_media",
            joinColumns = {@JoinColumn(name = "request_id")},
            inverseJoinColumns = {@JoinColumn(name = "origin_media_id")}
    )
    private Set<Media> originMedias = new HashSet<>();

    @Column(name="spec_comment")
    private String specComment;

    /*    @JoinColumn(name = "spec_media_id", unique = true)
        @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)*/
    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "request_spec_media",
            joinColumns = {@JoinColumn(name = "request_id")},
            inverseJoinColumns = {@JoinColumn(name = "spec_media_id")}
    )
    private Set<Media> specMedias = new HashSet<>();

    @Column(name="oetp")
    private String oetp;

    //@Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "request_operator",
            joinColumns = { @JoinColumn(name = "request_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> operators = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "request_technical_responsible",
            joinColumns = @JoinColumn(name ="request_id" , referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName="id")
    )
    private Set<User> technicalResponsibles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_family_id")
    private AircraftFamily aircraftFamily;

    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "request_aircraft_type",
            joinColumns = { @JoinColumn(name = "request_id") },
            inverseJoinColumns = { @JoinColumn(name = "aircraft_type_id") }
    )
    private Set<AircraftType> aircraftTypes = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "request_aircraft_version",
            joinColumns = { @JoinColumn(name = "request_id") },
            inverseJoinColumns = { @JoinColumn(name = "aircraft_version_id") }
    )
    private Set<AircraftVersion> aircraftVersions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_type_id")
    private MissionType missionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "environment_id")
    private Environment environment;

    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "request_client",
            joinColumns = { @JoinColumn(name = "request_id") },
            inverseJoinColumns = { @JoinColumn(name = "client_id") }
    )
    private Set<Client> clients = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.REMOVE},
            mappedBy="parentRequest")
    private Set<ChildRequest> childRequests = new HashSet<>();

    /**
     * Adds a client to the client making the request.
     *
     * @param client a Client
     */
    public void addClient(final Client client) {
        if (client != null) {
            clients.add(client);
        }
    }

    /**
     * Add an assignation operator.
     * @param user a user as operator
     */
    public void addOperator(final User user) {
        if (user != null) {
            operators.add(user);
        }
    }

    /**
     * Add an assignation technical responsible.
     * @param user a user as technical responsible
     */
    public void addTechnicalResponsible(final User user) {
        if (user != null) {
            this.technicalResponsibles.add(user);
            user.getRequests().add(this);
        }
    }

    /**
     * Add a child request.
     * @param childRequest a childrequest
     */
    public void addChildRequest(final ChildRequest childRequest) {
        if (childRequest != null) {
            childRequests.add(childRequest);
        }
    }

    public void removeChildRequest(final ChildRequest childRequest) {
        if (childRequest != null) {
            childRequests.remove(childRequest);
        }
    }

    /**
     * Adds a specMedia.
     *
     * @param specMedia a Media
     */
    public void addSpecMedia(final Media specMedia) {
        if (specMedia != null) {
            getSpecMedias().add(specMedia);
        }
    }

    /**
     * Adds a specMedia.
     *
     * @param originMedia a Media
     */
    public void addOriginMedia(final Media originMedia) {
        if (originMedia != null) {
            getOriginMedias().add(originMedia);
        }
    }

    /**
     * Adds an aircraftType.
     *
     * @param aircraftType a AircraftType
     */
    public void addAircraftType(final AircraftType aircraftType) {
        if (aircraftType != null) {
            getAircraftTypes().add(aircraftType);
        }
    }

    /**
     * Adds an aircraftVersion.
     *
     * @param aircraftVersion a AircraftVersion
     */
    public void addAircraftVersion(final AircraftVersion aircraftVersion) {
        if (aircraftVersion != null) {
            getAircraftVersions().add(aircraftVersion);
        }
    }
}
