package com.airbus.retex.dataset;

import com.airbus.retex.model.admin.airbus.AirbusEntity;
import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.ata.ATA;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.environment.Environment;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.functional.FunctionalAreaName;
import com.airbus.retex.model.functionality.Functionality;
import com.airbus.retex.model.functionality.damage.FunctionalityDamage;
import com.airbus.retex.model.inspection.Inspection;
import com.airbus.retex.model.material.Material;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.media.MediaTmp;
import com.airbus.retex.model.messaging.WebsocketIdentifier;
import com.airbus.retex.model.mesureUnit.MeasureUnit;
import com.airbus.retex.model.mission.MissionType;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.origin.Origin;
import com.airbus.retex.model.part.Mpn;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.part.PartDesignation;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.step.StepActivation;
import com.airbus.retex.model.task.TodoListName;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.model.treatment.Treatment;
import com.airbus.retex.model.user.User;

import java.io.Serializable;

public class Dataset implements Serializable {

    public static final String ADMIN_EMAIL = "pierre.martin@test.com";
    public static final String ADMIN_FIRSTNAME = "Pierre";
    public static final String USER_ONE_EMAIL = "jean.thomas@gmail.com";
    public static final String USER_ONE_FIRSTNAME = "jean";
    public static final String USER_TWO_EMAIL = "jeanlouis.dupont@test.com";
    public static final String USER_TWO_FIRSTNAME = "jean louis";
    public static final String FunctionalAreaNameEn_OuterRing = "Outer ring functionality_teeth bottom";
    public static final String FunctionalAreaNameEn_Ring = "Ring functionality_teeth bottom";
    public static final String FunctionalAreaNameFr_OuterRing = "Dents du bas de la bague extérieure";
    public static final String FunctionalAreaNameFr_Ring = "Dents du bas de la bague";

    public static final String USER_PART_READER_EMAIL = "jeanlouis.durand@test.com";
    public static final String USER_PART_READER_FIRSTNAME = "jean louis";
    public static final String USER_LASTNAME_DUPONT = "dupont";

    public static final String ATA_CODE_64 = "64";

    public static final String INVALID_LANGUAGE_CODE = "XY";
    /**
     * this user should be persisted with post_a changelog in order to be present in all
     * tests
     */
    public User user_superAdmin;
    public User user_simpleUser;
    public User user_simpleUser2;
    public User user_partReader;
    public User user_without_role;
    public User user_with_one_role;


    // ----------------------------------------------
    // ----------------ROLES ------------------------
    // ----------------------------------------------
    public Role role_admin;
    public Role role_technical_responsible;
    public Role role_quality_controller;
    public Role role_operator;
    public Role role_without_feature;
    // ----------------------------------------------
    // ----------------AIRBUS ENTITY ----------------
    // ----------------------------------------------
    public AirbusEntity airbusEntity_france;
    public AirbusEntity airbusEntity_canada;

    // ----------------------------------------------
    // ----------------FUNCTIONALITY ----------------
    // ----------------------------------------------
    public Functionality functionality_teeth;
    public Functionality functionality_bearingRaces;
    public Functionality functionality_bearingSeat;
    public Functionality functionality_splines;
    public Functionality functionality_bearingRings;
    public Functionality functionality_grooves;
    // ----------------------------------------------
    // ----------------MATERIAL ---------------------
    // ----------------------------------------------
    public Material material_16NCD13;
    public Material material_35NCD16;
    public Material material_15CN6;
    // ----------------------------------------------
    // ----------------TREATMENT --------------------
    // ----------------------------------------------
    public Treatment treatment_cooperPlating;
    public Treatment treatment_cadmiumPlating;
    // ----------------------------------------------
    // --------------------DAMAGE -------------------
    // ----------------------------------------------
    public Damage damage_corrosion;
    public Damage damage_indentation;
    public Damage damage_microPitting;
    public Damage damage_lightWear;
    public Damage damage_spalling;
    public Damage damage_crack;
    // ----------------------------------------------
    // --------------FUNCTIONAL AREA-----------------
    // ----------------------------------------------
    public FunctionalArea functionalArea_partOne;
    public FunctionalArea functionalArea_partTwo;
    // ----------------------------------------------
    // --------------FUNCTIONAL AREA FIELD_NAME------
    // ----------------------------------------------
    public FunctionalAreaName functionalAreaName_OuterRingBottom;
    public FunctionalAreaName functionalAreaName_RingBottom;
    // ----------------------------------------------
    // ---------FUNCTIONALITY DAMAGE-----------------
    // ----------------------------------------------
    public FunctionalityDamage corosionTeeth;
    public FunctionalityDamage corosionBearingRaces;
    public FunctionalityDamage indentationBearingRace;
    public FunctionalityDamage microPittingSplines;
    public FunctionalityDamage crackTeeth;
    public FunctionalityDamage crackGrooves;
    // ----------------------------------------------
    // ---------PART DESIGNATION---------------------
    // ----------------------------------------------
    public PartDesignation partDesignation_initialize;
    public PartDesignation partDesignation_planetGear;
    public PartDesignation partDesignation_lowerLeg;
    // ----------------------------------------------
    // -------------------ATA------------------------
    // ----------------------------------------------
    public ATA ata_initialize;
    public ATA ata_1;
    public ATA ata_2;
    // ----------------------------------------------
    // -----------------  PART ----------------------
    // ----------------------------------------------
    public Part part_example;
    public Part part_link_routing;
    public Part part_link_routing2;
    public Part part_link_routing3;
    public Part part_example_2;
    // ----------------------------------------------
    // ----------------- PHYSICAL PART --------------
    // ----------------------------------------------
    public PhysicalPart physicalPart_example;
    // ----------------------------------------------
    // ----------------- FILTERING-------------------
    // ----------------------------------------------
    public Filtering filtering_example;
    // ----------------------------------------------
    // -----------------  MPN -----------------------
    // ----------------------------------------------
    public Drt drt_example;
    // ----------------------------------------------
    // -----------------  MPN -----------------------
    // ----------------------------------------------
    public Mpn mpn_1;
    public Mpn mpn_2;
    // ----------------------------------------------
    // -----------------  FA-FIELD_NAME -------------
    // ----------------------------------------------
    public FunctionalAreaName functionalAreaName_1;
    public FunctionalAreaName functionalAreaName_2;
    // ----------------------------------------------
    // ---------   FUNCTIONAL AREA ------------------
    // ----------------------------------------------
    public FunctionalArea functionalArea_1;
    public FunctionalArea functionalArea_2;
    // ----------------------------------------------
    // ------------   MEASURE UNIT ------------------
    // ----------------------------------------------
    public MeasureUnit measureUnit_mm;
    public MeasureUnit measureUnit_mm2;
    // ----------------------------------------------
    // ---------------   POST -----------------------
    // ----------------------------------------------
    public Post post_quantity;
    public Post post_a;
    public Post post_b;
    public Post post_c;
    public Post post_d;
    // ----------------------------------------------
    // ------------- ROUTING COMPONENT --------------
    // ----------------------------------------------
    public RoutingComponent routingComponent;
    public RoutingComponent routingComponent_visual;
    public RoutingComponent routingComponent_todo_list;
    public RoutingComponent routingComponent_generic;
    // ----------------------------------------------
    // ------------- OPERATION TYPE --------------
    // ----------------------------------------------
    public OperationType operationType_visual;
    public OperationType operationType_dimensional;
    public OperationType operationType_tridimensional;
    public OperationType operationType_preliminary;
    public OperationType operationType_laboratory;
    public OperationType operationType_closure;
    public OperationType operationType_generic_undefined;
    public OperationType operationType_todolist;
    // ----------------------------------------------
    // ------------- INSPECTION ---------------------
    // ----------------------------------------------
    public Inspection inspection_internal;
    public Inspection inspection_external;
    public Inspection inspection_generic;
    // ----------------------------------------------
    // ------------- TASK ---------------------------
    // ----------------------------------------------
    public TodoListName todoListName_1;
    public TodoListName todoListName_2;
    public TodoListName todoListName_3;
    // ----------------------------------------------
    // ------------- TODOLIST -----------------------
    // ----------------------------------------------
    public TodoList todoList_1;
    public TodoList todoList_2;
    public TodoList todoList_3;
    // ----------------------------------------------
    // ------------- ROUTING COMPONENT INDEX---------
    // ----------------------------------------------
    public RoutingComponentIndex routingComponentIndex_1;
    public RoutingComponentIndex routingComponentIndex_2;
    public RoutingComponentIndex routingComponentIndex_3;
    // ----------------------------------------------
    // ------------- ROUTING ------------------------
    // ----------------------------------------------
    public Routing routing_1;
    public Routing routing_2;
    public Routing routing_3;
    // ----------------------------------------------
    // ------------- OPERATION ----------------------
    // ----------------------------------------------
    public Operation operation_1;
    public Operation operation_2;
    public Operation operation_3_todo_list;
    // ----------------------------------------------
    // -----------------  MISSION -------------------
    // ----------------------------------------------
    public MissionType mission_1;
    public MissionType mission_2;
    // ----------------------------------------------
    // ------------- REQUEST ------------------------
    // ----------------------------------------------
    public Request request_1;
    public Request request_2;
    public Request request_update;
    public Request request_save;
    // ----------------------------------------------
    // ------------- CHILD REQUEST ------------------
    // ----------------------------------------------
    public ChildRequest childRequest_1;
    // ----------------------------------------------
    // -----------------  ENVIRONMENT ---------------
    // ----------------------------------------------
    public Environment environment_1;
    public Environment environment_2;
    // ----------------------------------------------
    // -----------------  CLIENT --------------------
    // ----------------------------------------------
    public Client client_1;
    public Client client_2;
    // ----------------------------------------------
    // -----------------  AIRCRAFT FAMILY-----------------------
    // ----------------------------------------------
    public AircraftFamily aircraft_family_1;
    public AircraftFamily aircraft_family_2;
    public AircraftFamily aircraft_family_3;
    public AircraftFamily aircraft_set_family_1;
    public AircraftFamily aircraft_set_family_2;

    // ----------------------------------------------
    // -----------------  AIRCRAFT TYPE -----------------------
    // ----------------------------------------------
    public AircraftType aircraft_type_1;
    public AircraftType aircraft_type_2;
    public AircraftType aircraft_type_3;
    public AircraftType aircraft_set_family_1_type_1;
    public AircraftType aircraft_set_family_1_type_2;
    public AircraftType aircraft_set_family_2_type_1;

    // ----------------------------------------------
    // -----------------  AIRCRAFT VERSION-----------------------
    // ----------------------------------------------
    public AircraftVersion aircraft_version_1;
    public AircraftVersion aircraft_version_2;
    public AircraftVersion aircraft_version_3;
    public AircraftVersion aircraft_set_family_1_type_1_version_1;
    public AircraftVersion aircraft_set_family_1_type_1_version_2;
    public AircraftVersion aircraft_set_family_1_type_2_version_1;
    public AircraftVersion aircraft_set_family_2_type_1_version_1;

    // ----------------------------------------------
    // ------------- ORIGIN -------------------------
    // ----------------------------------------------
    public Origin ORIGIN_CIVP;
    public Origin ORIGIN_ISIR;
    public Origin ORIGIN_RETEX;
    // ----------------------------------------------
    // ------------- STEP ---------------------------
    // ----------------------------------------------
    public Step step_one;
    public Step step_two;
    public Step step_three;

    // ----------------------------------------------
    // ------------- STEP ACTIVATION ----------------
    // ----------------------------------------------
    public StepActivation stepActivation_one;
    public StepActivation stepActivation_two;
    public StepActivation stepActivation_three;
    // ----------------------------------------------
    // --------- OPERATION FUNCTIONAL AREA ----------
    // ----------------------------------------------
    public OperationFunctionalArea operationFunctionalArea_one;
    public OperationFunctionalArea operationFunctionalArea_two;
    public OperationFunctionalArea operationFunctionalArea_three;
    // ----------------------------------------------
    // -------------------------MEDIA ---------------
    // ----------------------------------------------
    public Media media_1;
    public MediaTmp mediaTmp_1;
    // ----------------------------------------------
    // ---------------------WEBSOCKET CLIENT ---------
    // ----------------------------------------------
    public WebsocketIdentifier websocketIdentifier_one;


    public int next = 0;

}
