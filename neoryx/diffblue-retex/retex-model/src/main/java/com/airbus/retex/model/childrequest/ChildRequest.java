package com.airbus.retex.model.childrequest;

import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.model.basic.AbstractHistorizableBaseModel;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.environment.Environment;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.mission.MissionType;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the child_request database table.
 */
@Entity
@Table(name = "child_request")
@Audited
@Getter
@Setter
public class ChildRequest extends AbstractHistorizableBaseModel {

    private static final long serialVersionUID = 637669990110397756L;

    // ----------------------- drts ----------------------------

    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "childRequest")
    private Set<Drt> drts = new HashSet<>();

    // ----------------------- clients ----------------------------

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "child_request_client",
            joinColumns = { @JoinColumn(name = "child_request_id") },
            inverseJoinColumns = { @JoinColumn(name = "client_id") }
    )
    private Set<Client> clients = new HashSet<>();

    // --------------------- parent request ---------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_request_id")
    private Request parentRequest;

    // ----------------------- mission type  ----------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_type_id")
    private MissionType missionType;

    // ----------------------- environement ----------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "environment_id")
    private Environment environment;

    // ----------------------- aircraft family -----------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_family_id")
    private AircraftFamily aircraftFamily;

    // ----------------------- aircraft type -----------------------

    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "child_request_aircraft_type",
            joinColumns = { @JoinColumn(name = "child_request_id") },
            inverseJoinColumns = { @JoinColumn(name = "aircraft_type_id") }
    )
    private Set<AircraftType> aircraftTypes = new HashSet<>();

    // ----------------------- aircraft version -----------------------

    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "child_request_aircraft_version",
            joinColumns = { @JoinColumn(name = "child_request_id") },
            inverseJoinColumns = { @JoinColumn(name = "aircraft_version_id") }
    )
    private Set<AircraftVersion> aircraftVersions = new HashSet<>();


    // ----------------------- routing  ----------------------------
    // ROUTING is deduced by PART, do we need this ?
    @Column(name = "routing_id")
    private Long routingNaturalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula(value = "(SELECT r.id FROM routing r WHERE r.natural_id = routing_id AND r.status = 'VALIDATED')")
    @NotAudited
    private Routing routing;

    // ----------------------- media  ----------------------------

   @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
   @JoinTable(
       name = "child_request_media",
       joinColumns = { @JoinColumn(name = "child_request_id") },
       inverseJoinColumns = { @JoinColumn(name = "media_id") }
   )
    private Set<Media> medias = new HashSet<>();

    // ----------------------- drt to inspect ----------------------

    @Column(name = "drt_to_inspect", nullable = true)
    private Long drtToInspect;

    // ------------------------- modulation -------------------------
    // Modulation is deleted : see screen 8.3.1 that is the update of screen 8.3.0
    @Column(name = "modulation")
    private Integer modulation;

    // ----------------------- Serial Numbers -----------------------

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "childRequest")
    private Set<PhysicalPart> physicalParts = new HashSet<>();


    public void addPhysicalPart(final PhysicalPart physicalPart) {
        if (physicalPart != null) {
            physicalParts.add(physicalPart);
            physicalPart.setChildRequest(this);
        }
    }



    public void addClient(final Client client) {
        if (client != null) {
            clients.add(client);
            client.getChildRequests().add(this);
        }
    }



    public void addDrt(final Drt drt) {
        if (drt != null){
            getDrts().add(drt);
        }
    }


    public void addMedia(final Media media) {
        if (media != null){
            getMedias().add(media);
        }
    }


    public void addAircraftType(final AircraftType aircraftType) {
        if (aircraftType != null) {
            getAircraftTypes().add(aircraftType);
        }
    }


    public void addAircraftVersion(final AircraftVersion aircraftVersion) {
        if (aircraftVersion != null) {
            getAircraftVersions().add(aircraftVersion);
        }
    }

}