package com.airbus.retex.dataset;

import com.airbus.retex.business.dto.functionality.FunctionalityFieldsEnum;
import com.airbus.retex.business.dto.inspection.InspectionEnumFields;
import com.airbus.retex.business.dto.measureUnit.MeasureUnitsFieldsEnum;
import com.airbus.retex.business.dto.operationType.OperationTypeFieldsEnum;
import com.airbus.retex.business.dto.role.RoleFieldEnum;
import com.airbus.retex.business.dto.todoListName.TodoListNameFieldsEnum;
import com.airbus.retex.business.dto.treatment.TreatmentFieldsEnum;
import com.airbus.retex.model.AbstractTranslation;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.airbus.AirbusEntity;
import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.ata.ATA;
import com.airbus.retex.model.basic.IIdentifiedModel;
import com.airbus.retex.model.basic.IIdentifiedVersionModel;
import com.airbus.retex.model.basic.IModel;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.classification.EnumClassification;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.common.*;
import com.airbus.retex.model.control.ControlRoutingComponent;
import com.airbus.retex.model.control.ControlTodoList;
import com.airbus.retex.model.control.ControlVisual;
import com.airbus.retex.model.control.EnumTodoListValue;
import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.model.damage.DamageFieldsEnum;
import com.airbus.retex.model.damage.DamageTranslation;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.drt.DrtOperationStatusTodoList;
import com.airbus.retex.model.drt.DrtPictures;
import com.airbus.retex.model.drt.DrtOperationStatusFunctionalArea;
import com.airbus.retex.model.environment.Environment;
import com.airbus.retex.model.environment.EnvironmentFieldsEnum;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.functional.FunctionalAreaName;
import com.airbus.retex.model.functionality.Functionality;
import com.airbus.retex.model.functionality.damage.FunctionalityDamage;
import com.airbus.retex.model.functionality.damage.FunctionalityDamageFieldsEnum;
import com.airbus.retex.model.functionality.damage.FunctionalityDamageTranslation;
import com.airbus.retex.model.inspection.Inspection;
import com.airbus.retex.model.material.Material;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.media.MediaTmp;
import com.airbus.retex.model.messaging.WebsocketIdentifier;
import com.airbus.retex.model.mesureUnit.MeasureUnit;
import com.airbus.retex.model.mission.MissionType;
import com.airbus.retex.model.mission.MissionTypeFieldsEnum;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.model.origin.Origin;
import com.airbus.retex.model.part.Mpn;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.part.PartDesignation;
import com.airbus.retex.model.part.PartDesignationFieldsEnum;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.post.PostFieldsEnum;
import com.airbus.retex.model.post.PostTranslation;
import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import com.airbus.retex.model.qcheck.QcheckRoutingComponent;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.request.RequestFieldsEnum;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routing.RoutingFieldsEnum;
import com.airbus.retex.model.routing.RoutingTranslation;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentFieldsEnum;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.step.StepActivation;
import com.airbus.retex.model.step.StepFieldsEnum;
import com.airbus.retex.model.step.StepTranslation;
import com.airbus.retex.model.task.TodoListName;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.model.todoList.TodoListFieldsEnum;
import com.airbus.retex.model.translation.Translate;
import com.airbus.retex.model.treatment.Treatment;
import com.airbus.retex.model.user.User;
import com.airbus.retex.model.user.UserFeature;
import com.airbus.retex.model.user.UserRole;
import com.airbus.retex.persistence.Step.StepActivationRepository;
import com.airbus.retex.persistence.admin.*;
import com.airbus.retex.persistence.airbus.AirbusEntityRepository;
import com.airbus.retex.persistence.aircraft.AircraftFamilyRepository;
import com.airbus.retex.persistence.aircraft.AircraftTypeRepository;
import com.airbus.retex.persistence.aircraft.AircraftVersionRepository;
import com.airbus.retex.persistence.ata.ATARepository;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.childRequest.PhysicalPartRepository;
import com.airbus.retex.persistence.client.ClientRepository;
import com.airbus.retex.persistence.control.ControlRoutingComponentRepository;
import com.airbus.retex.persistence.control.ControlTodoListRepository;
import com.airbus.retex.persistence.control.ControlVisualRepository;
import com.airbus.retex.persistence.damage.DamageRepository;
import com.airbus.retex.persistence.damage.functionality.FunctionalityDamageRepository;
import com.airbus.retex.persistence.drt.DrtOperationStatusRepository;
import com.airbus.retex.persistence.drt.DrtPicturesRepository;
import com.airbus.retex.persistence.drt.DrtRepository;
import com.airbus.retex.persistence.drt.QcheckRoutingComponentRepository;
import com.airbus.retex.persistence.environment.EnvironmentRepository;
import com.airbus.retex.persistence.filtering.FilteringRepository;
import com.airbus.retex.persistence.functionalArea.FunctionalAreaNameRepository;
import com.airbus.retex.persistence.functionalArea.FunctionalAreaRepository;
import com.airbus.retex.persistence.functionality.FunctionalityRepository;
import com.airbus.retex.persistence.inspection.InspectionRepository;
import com.airbus.retex.persistence.material.MaterialRepository;
import com.airbus.retex.persistence.measureUnit.MeasureUnitRepository;
import com.airbus.retex.persistence.messaging.WebsocketIdentifierRepository;
import com.airbus.retex.persistence.mission.MissionTypeRepository;
import com.airbus.retex.persistence.mpn.MpnRepository;
import com.airbus.retex.persistence.operation.OperationFunctionalAreaRepository;
import com.airbus.retex.persistence.operation.OperationRepository;
import com.airbus.retex.persistence.operationType.OperationTypeRepository;
import com.airbus.retex.persistence.origin.OriginRepository;
import com.airbus.retex.persistence.part.PartDesignationRepository;
import com.airbus.retex.persistence.part.PartRepository;
import com.airbus.retex.persistence.post.RoutingFunctionalAreaPostRepository;
import com.airbus.retex.persistence.request.RequestRepository;
import com.airbus.retex.persistence.routing.RoutingRepository;
import com.airbus.retex.persistence.routingComponent.RoutingComponentIndexRepository;
import com.airbus.retex.persistence.routingComponent.RoutingComponentRepository;
import com.airbus.retex.persistence.todoList.TodoListNameRepository;
import com.airbus.retex.persistence.todoList.TodoListRepository;
import com.airbus.retex.persistence.translate.TranslateRepository;
import com.airbus.retex.persistence.treatment.TreatmentRepository;
import com.airbus.retex.service.impl.util.HibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.airbus.retex.dataset.Dataset.*;

@Component
@Transactional
public class DatasetInitializer implements IDatasetInitializer {
    @Autowired
    private org.springframework.core.env.Environment environment;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TranslateRepository translateRepository;
    @Autowired
    private PartRepository partRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleFeatureRepository roleFeatureRepository;
    @Autowired
    private AirbusEntityRepository airbusEntityRepository;
    @Autowired
    private FunctionalityRepository functionalityRepository;
    @Autowired
    private TreatmentRepository treatmentRepository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserFeatureRepository userFeatureRepository;
    @Autowired
    private DamageRepository damageRepository;
    @Autowired
    private FunctionalityDamageRepository functionalityDamageRepository;
    @Autowired
    private PartDesignationRepository partDesignationRepository;
    @Autowired
    private ATARepository ataRepository;
    @Autowired
    private MpnRepository mpnRepository;
    @Autowired
    private FunctionalAreaRepository functionalAreaRepository;
    @Autowired
    private FunctionalAreaNameRepository functionalAreaNameRepository;
    @Autowired
    private MeasureUnitRepository measureUnitRepository;
    @Autowired
    private RoutingComponentRepository routingComponentRepository;
    @Autowired
    private OperationTypeRepository operationTypeRepository;
    @Autowired
    private InspectionRepository inspectionRepository;
    @Autowired
    private RoutingComponentIndexRepository routingComponentIndexRepository;
    @Autowired
    private TodoListNameRepository todoListNameRepository;
    @Autowired
    private TodoListRepository todoListRepository;
    @Autowired
    private RoutingRepository routingRepository;
    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private OperationFunctionalAreaRepository operationFunctionalAreaRepository;
    @Autowired
    private RoutingFunctionalAreaPostRepository routingFunctionalAreaPostRepository;
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private MediaTemporaryRepository mediaTemporaryRepository;
    @Autowired
    private MissionTypeRepository missionTypeRepository;
    @Autowired
    private EnvironmentRepository environmentRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AircraftFamilyRepository aircraftFamilyRepository;
    @Autowired
    private AircraftTypeRepository aircraftTypeRepository;
    @Autowired
    private AircraftVersionRepository aircraftVersionRepository;
    @Autowired
    private PhysicalPartRepository physicalPartRepository;
    @Autowired
    private FilteringRepository filteringRepository;

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private ChildRequestRepository childRequestRepository;
    @Autowired
    private DrtRepository drtRepository;
    @Autowired
    private DrtPicturesRepository drtPicturesRepository;
    @Autowired
    private OriginRepository originRepository;
    @Autowired
    private StepActivationRepository stepActivationRepository;

    @Autowired
    private ControlVisualRepository controlVisualRepository;
    @Autowired
    private ControlRoutingComponentRepository controlRoutingComponentRepository;
    @Autowired
    private ControlTodoListRepository controlTodoListRepository;
    @Autowired
    private DrtOperationStatusRepository drtOperationStatusRepository;
    @Autowired
    private QcheckRoutingComponentRepository qcheckRoutingComponentRepository;

    private Dataset dataset;

    @Autowired
    private WebsocketIdentifierRepository websocketIdentifierRepository;
    public static final String SERIAL_NUMBER_PHYSICAL_PART = "123456";
    public static final String SERIAL_NUMBER_AIRCRAFT = "654321";

    private static Long versionNumber = 1L;

    @Override
    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public Dataset getDataset() {return dataset;}

    public synchronized int getNext() {
        dataset.next = dataset.next + 1;
        return dataset.next;
    }

    public String getNextCode(int length) {
        if (length > 10) {
            throw new IllegalArgumentException("Can't generate such long code with current implementation");
        }
        if (dataset.next > 1000000000) {
            throw new IllegalStateException("Can't generate more code with current implementation");
        }
        return String.valueOf(((long) getNext()) * 1000000000).substring(0, length);
    }

    public <T extends IIdentifiedModel> T reference(T detachedEntity) {
       return (T) entityManager.getReference(HibernateUtil.getPersistentClass(detachedEntity), detachedEntity.getId());
    }

    public <T extends IIdentifiedVersionModel> T reference(T detachedEntity) {
        return (T) entityManager.getReference(HibernateUtil.getPersistentClass(detachedEntity), detachedEntity.getTechnicalId());
    }

    @Override
    public void initDataset() {
        // -------------------------------------------------------
        // -----------------CREATE Airbus entities----------------
        // -------------------------------------------------------
        dataset.airbusEntity_france = createAirBusEntity("fr0001", "France");
        dataset.airbusEntity_canada = createAirBusEntity("ca001", "Canada");

        // -----------------------------------------------------------------------------------------------------
        // ------------------------------------- CREATE ROLES --------------------------------------------------
        //------------------------------------------------------------------------------------------------------

        dataset.role_admin = createRoleFromConfig("retex.sso.roleIdMapping.administrator");
        dataset.role_technical_responsible = createRoleFromConfig("retex.sso.roleIdMapping.technical_responsible");
        dataset.role_operator = createRoleFromConfig("retex.sso.roleIdMapping.internal_operator");
        dataset.role_quality_controller = createRoleFromConfig("retex.sso.roleIdMapping.quality_controler");
        dataset.role_without_feature = createRole(dataset.airbusEntity_france, RoleCode.ROLE_WITHOUT_FEATURE);

        // -----------------------------------------------------------------------------------------------------
        // ------------------------------------- CREATE USERS --------------------------------------------------
        //------------------------------------------------------------------------------------------------------

        dataset.user_superAdmin = createUser(dataset.airbusEntity_france, user -> {
            user.setEmail(ADMIN_EMAIL);
            user.setFirstName(USER_TWO_FIRSTNAME);
            user.setLastName(USER_LASTNAME_DUPONT);
            // set examples of roles
            user.addRole(dataset.role_admin);
        });

        dataset.user_partReader = createUser(dataset.airbusEntity_france, user -> {
            user.setEmail(USER_PART_READER_EMAIL);
            user.setFirstName(USER_PART_READER_FIRSTNAME);
            // set examples of roles
            user.addRole(dataset.role_operator);
            user.addRole(dataset.role_technical_responsible);
        });

        dataset.user_simpleUser = createUser(dataset.airbusEntity_canada, user -> {
            user.setEmail(USER_ONE_EMAIL);
            user.setFirstName(USER_ONE_FIRSTNAME);
            // set examples of roles
            user.addRole(dataset.role_without_feature);
        });

        dataset.user_simpleUser2 = createUser(dataset.airbusEntity_france, user -> {
            user.setEmail(USER_TWO_EMAIL);
            user.setFirstName(USER_TWO_FIRSTNAME);
            user.setLastName(USER_LASTNAME_DUPONT);
            // set examples of roles
            user.addRole(dataset.role_without_feature);
        });
        dataset.user_with_one_role = createUser(dataset.airbusEntity_france, user -> {
            user.addRole(roleRepository.findById(2L).get());
        });
        dataset.user_without_role = createUser(dataset.airbusEntity_france, user -> {});

        // -------------------------------------------------------
        // ---------------------Material -------------------------
        // -------------------------------------------------------
        dataset.material_15CN6 = createMaterial("15CN6");
        dataset.material_16NCD13 = createMaterial("16NCD13");
        dataset.material_35NCD16 = createMaterial("35NCD16");

        // -------------------------------------------------------
        // ---------------------Treatment ------------------------
        // -------------------------------------------------------
        dataset.treatment_cadmiumPlating = createTreatment();
        dataset.treatment_cooperPlating = createTreatment();

        dataset.functionality_teeth = createFunctionality();
        dataset.functionality_bearingRaces = createFunctionality();

        // -------------------------------------------------------
        // -----------------CREATE FUNCTIONALITIES----------------
        // -------------------------------------------------------
        dataset.functionality_teeth = createFunctionality();
        dataset.functionality_bearingRaces = createFunctionality();
        dataset.functionality_bearingSeat = createFunctionality();
        dataset.functionality_splines = createFunctionality();
        dataset.functionality_bearingRings = createFunctionality();
        dataset.functionality_grooves = createFunctionality();

        // -------------------------------------------------------
        // -----------------CREATE DAMAGES------------------------
        // -------------------------------------------------------
        dataset.damage_corrosion = createDamage(EnumActiveState.ACTIVE, "corrosion");
        dataset.damage_indentation = createDamage(EnumActiveState.ACTIVE, "indentation");
        dataset.damage_microPitting = createDamage(EnumActiveState.ACTIVE, "microPitting");
        dataset.damage_lightWear = createDamage(EnumActiveState.ACTIVE, "lightWear");
        dataset.damage_spalling = createDamage(EnumActiveState.ACTIVE, "spalling");
        dataset.damage_crack = createDamage(EnumActiveState.REVOKED, "crack");
        // -------------------------------------------------------
        // -----------------CREATE FUNCTIONALITY DAMAGES----------
        // -------------------------------------------------------
        dataset.corosionTeeth = createFunctionalityDamage(dataset.functionality_teeth, dataset.damage_corrosion);
        dataset.corosionBearingRaces = createFunctionalityDamage(dataset.functionality_bearingRaces, dataset.damage_corrosion);
        dataset.indentationBearingRace = createFunctionalityDamage(dataset.functionality_bearingRaces, dataset.damage_indentation);
        dataset.microPittingSplines = createFunctionalityDamage(dataset.functionality_splines, dataset.damage_microPitting);
        dataset.crackTeeth = createFunctionalityDamage(dataset.functionality_teeth, dataset.damage_crack);
        dataset.crackGrooves = createFunctionalityDamage(dataset.functionality_grooves, dataset.damage_crack);

        // -------------------------------------------------------
        // -----------------CREATE Part Designation --------------
        // -------------------------------------------------------
        dataset.partDesignation_initialize = createPartDesignation(null,null);
        dataset.partDesignation_planetGear = createPartDesignation(null,null);
        dataset.partDesignation_lowerLeg = createPartDesignation(null,null);
        // -------------------------------------------------------
        // -----------------CREATE ATA----------------------------
        // -------------------------------------------------------
        dataset.ata_initialize = createATA();
        dataset.ata_1 = createATA(
                ata -> {
                    ata.setCode("A1");
                }
        );
        dataset.ata_2 = createATA(
                ata -> {
                    ata.setCode("A2");
                });

        // -------------------------------------------------------
        // -----------------CREATE MPN----------------------------
        // -------------------------------------------------------
        dataset.mpn_1 = createMpn();
        dataset.mpn_2 = createMpn();

        // -------------------------------------------------------
        // -----------------CREATE PART---------------------------
        // -------------------------------------------------------
        dataset.part_example = createPart(null);
        dataset.part_link_routing = createPart(part -> {
            part.setPartNumber("3334598");
            part.setStatus(EnumStatus.VALIDATED);
        }, null);
        dataset.part_link_routing2 = createPart(null);
        dataset.part_link_routing3 = createPart(null);
        dataset.part_example_2 = createPart(null);

        // -------------------------------------------------------
        // -----------------CREATE PHYSICAL PART------------------
        // -------------------------------------------------------
        dataset.physicalPart_example = createPhysicalPart();
        // -------------------------------------------------------
        // ----------------- CREATE FILTERING---------------------
        // -------------------------------------------------------
        dataset.filtering_example = createFiltering();
        // -------------------------------------------------------
        // -----------------Functional Area-Name------------------
        // -------------------------------------------------------
        dataset.functionalAreaName_OuterRingBottom = createFunctionalAreaName(dataset.FunctionalAreaNameEn_OuterRing, dataset.FunctionalAreaNameFr_OuterRing);
        dataset.functionalAreaName_RingBottom = createFunctionalAreaName(dataset.FunctionalAreaNameEn_Ring, dataset.FunctionalAreaNameFr_Ring);
        dataset.functionalAreaName_1 = createFunctionalAreaName();
        dataset.functionalAreaName_2 = createFunctionalAreaName();

        // -------------------------------------------------------
        // -----------------Functional Area-----------------------
        // -------------------------------------------------------
        dataset.functionalArea_1 = createFunctionalArea();
        dataset.functionalArea_2 = createFunctionalArea();
        dataset.functionalArea_partOne= createFunctionalArea();
        dataset.functionalArea_partTwo= createFunctionalArea();

        // ----------------------------------------------
        // ------------   MEASURE UNIT ------------------
        // ----------------------------------------------
        dataset.measureUnit_mm = createMeasureUnit("mm", "mm");
        dataset.measureUnit_mm2 = createMeasureUnit("mm2", "mm2");

        // ----------------------------------------------
        // ------------- OPERATION TYPE -----------------
        // ----------------------------------------------
        dataset.operationType_visual = createOperationType(operationType -> {
            operationType.setTemplate("visual");
            operationType.setBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT);
        }, "Visuel", "Visual");
        dataset.operationType_dimensional = createOperationType(operationType -> {
            operationType.setTemplate("dimensional");
            operationType.setBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT);
        }, "Dimensionel", "Dimensional");
        dataset.operationType_tridimensional = createOperationType(operationType -> {
            operationType.setTemplate("tridimensional");
            operationType.setBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT);
        }, "TriDimensionel", "TriDimensional");
        dataset.operationType_laboratory = createOperationType(operationType -> {
            operationType.setTemplate("laboratory");
            operationType.setBehavior(OperationTypeBehaviorEnum.ROUTING_COMPONENT);
        }, "Laboratoire", "Laboratory");
        dataset.operationType_preliminary = createOperationType(operationType -> {
            operationType.setTemplate("preliminary");
            operationType.setBehavior(OperationTypeBehaviorEnum.TODO_LIST);
        }, "Préliminaire", "Preliminary");
        dataset.operationType_closure = createOperationType(operationType -> {
            operationType.setTemplate("closure");
            operationType.setBehavior(OperationTypeBehaviorEnum.TODO_LIST);
        }, "Fermeture", "Closure");
        dataset.operationType_generic_undefined = createOperationType(operationType -> {
            operationType.setTemplate("Generic");
            operationType.setBehavior(OperationTypeBehaviorEnum.GENERIC);
        }, "Générique", "Generic");
        dataset.operationType_todolist = createOperationType(operationType -> {
            operationType.setTemplate("Generic");
            operationType.setBehavior(OperationTypeBehaviorEnum.TODO_LIST);
        }, "Générique", "Generic");

        // ----------------------------------------------
        // ------------- INSPECTION ---------------------
        // ----------------------------------------------
        dataset.inspection_internal = createInspection("Internal", "Interne");
        dataset.inspection_external = createInspection(inspection -> inspection.setValue("external"), "External", "Externe");
        dataset.inspection_generic = createInspection(inspection -> inspection.setValue("generic"), "Generic", "Générique");

        // ----------------------------------------------
        // ------------- ROUTING COMPONENT --------------
        // ----------------------------------------------
        dataset.routingComponent = createRoutingComponent(routingComponent -> {
            routingComponent.setOperationType(dataset.operationType_dimensional);
        });
        dataset.routingComponent_visual = createRoutingComponent(routingComponent -> {
            routingComponent.setOperationType(dataset.operationType_visual);
        });
        dataset.routingComponent_todo_list = createRoutingComponent(routingComponent -> {
            routingComponent.setOperationType(dataset.operationType_todolist);
        });

        // ----------------------------------------------
        // ------------- ROUTING COMPONENT --------------
        // ----------------------------------------------
        dataset.todoListName_1 = createTodoListName();
        dataset.todoListName_2 = createTodoListName();
        dataset.todoListName_3 = createTodoListName();

        // ----------------------------------------------
        // -------------  TODO LIST  --------------------
        // ----------------------------------------------
        dataset.todoList_1 = createTodoList();
        createRoutingComponentIndex(null, dataset.todoList_1.getTechnicalId());

        dataset.todoList_2 = createTodoList();
        createRoutingComponentIndex(null, dataset.todoList_2.getTechnicalId());

        dataset.todoList_3 = createTodoList();
        createRoutingComponentIndex(null, dataset.todoList_3.getTechnicalId());

        // ----------------------------------------------
        // ------------- ROUTING COMPONENT --------------
        // ----------------------------------------------
        dataset.routingComponentIndex_1 = createRoutingComponentIndex(dataset.routingComponent.getTechnicalId(), null);
        dataset.routingComponentIndex_2 = createRoutingComponentIndex(dataset.routingComponent.getTechnicalId(), null);
        dataset.routingComponentIndex_3 = createRoutingComponentIndex(dataset.routingComponent.getTechnicalId(), null);

        // ----------------------------------------------
        // ------------- ROUTING ------------------------
        // ----------------------------------------------
        dataset.routing_1 = createRouting(dataset.part_link_routing);
        dataset.routing_2 = createRouting(dataset.part_link_routing2);
        dataset.routing_3 = createRouting(dataset.part_link_routing3);
        // ----------------------------------------------
        // ------------- OPERATION ----------------------
        // ----------------------------------------------
        dataset.operation_1 = createOperation(1);
        dataset.operation_2 = createOperation(2);
        dataset.operation_3_todo_list = createOperation(3, operation -> {
            operation.setOperationType(dataset.operationType_preliminary);
        });

        // ----------------------------------------------
        // ------------- MEDIA --------------------------
        // ----------------------------------------------
        dataset.media_1 = createMedia();

        // ----------------------------------------------
        // ------------- MEDIA TEMP ---------------------
        // ----------------------------------------------
        dataset.mediaTmp_1 = createTemporaryMedia();

        // ----------------------------------------------
        // ------------- MISSION ------------------------
        // ----------------------------------------------
        dataset.mission_1 = createMissionType();
        dataset.mission_2 = createMissionType();

        // ----------------------------------------------
        // ------------- ENVIRONMENT --------------------
        // ----------------------------------------------
        dataset.environment_1 = createEnvironment();
        dataset.environment_2 = createEnvironment();

        // ----------------------------------------------
        // ------------- CLIENT -------------------------
        // ----------------------------------------------
        dataset.client_1 = createClient("Client 1");
        dataset.client_2 = createClient("Client 2");

        // ----------------------------------------------
        // ------------- AIRCRAFT FAMILY-----------------------
        // ----------------------------------------------
        dataset.aircraft_family_1 = createAircraftFamily("AS332");
        dataset.aircraft_family_2 = createAircraftFamily("AS532");
        dataset.aircraft_family_3 = createAircraftFamily("EC175");
        dataset.aircraft_set_family_1 = createAircraftFamily(af->{
            af.setName("f1");
        });
        dataset.aircraft_set_family_2 =  createAircraftFamily(af->{
            af.setName("f2");
        });

        // ----------------------------------------------
        // ------------- AIRCRAFT TYPE-----------------------
        // ----------------------------------------------
        dataset.aircraft_type_1 = createAircraftType("Super PUMA", aircraftType -> {
            aircraftType.setAircraftFamily(dataset.aircraft_family_1);
            aircraftType.setAircraftFamilyId(dataset.aircraft_family_1.getId());
        });
        dataset.aircraft_type_2 = createAircraftType("Super PUMA", aircraftType -> {
            aircraftType.setAircraftFamily(dataset.aircraft_family_2);
            aircraftType.setAircraftFamilyId(dataset.aircraft_family_2.getId());
        });
        dataset.aircraft_type_3 = createAircraftType("EC175", aircraftType -> {
            aircraftType.setAircraftFamily(dataset.aircraft_family_3);
            aircraftType.setAircraftFamilyId(dataset.aircraft_family_3.getId());
        });
        dataset.aircraft_set_family_1_type_1 = createAircraftType("f1t1", at->{
            at.setAircraftFamilyId(dataset.aircraft_set_family_1.getId());
            at.setAircraftFamily(dataset.aircraft_set_family_1);
        });
        dataset.aircraft_set_family_1_type_2 = createAircraftType("f1t2", at->{
            at.setAircraftFamilyId(dataset.aircraft_set_family_1.getId());
            at.setAircraftFamily(dataset.aircraft_set_family_1);
        });
        dataset.aircraft_set_family_2_type_1 = createAircraftType("f2t1", at->{
            at.setAircraftFamilyId(dataset.aircraft_set_family_2.getId());
            at.setAircraftFamily(dataset.aircraft_set_family_2);
        });

        // ----------------------------------------------
        // ------------- AIRCRAFT VERSION-----------------------
        // ----------------------------------------------
        dataset.aircraft_version_1 = createAircraftVersion("B", aircraftVersion -> {
            aircraftVersion.setAircraftType(dataset.aircraft_type_1);
            aircraftVersion.setAircraftTypeId(dataset.aircraft_type_1.getId());
        });
        dataset.aircraft_version_2 = createAircraftVersion("A", aircraftVersion -> {
            aircraftVersion.setAircraftType(dataset.aircraft_type_2);
            aircraftVersion.setAircraftTypeId(dataset.aircraft_type_2.getId());
        });
        dataset.aircraft_version_3 = createAircraftVersion("B", aircraftVersion -> {
            aircraftVersion.setAircraftType(dataset.aircraft_type_3);
            aircraftVersion.setAircraftTypeId(dataset.aircraft_type_3.getId());
        });
        dataset.aircraft_set_family_1_type_1_version_1 = createAircraftVersion("f1t1v1", av->{
            av.setAircraftTypeId(dataset.aircraft_set_family_1_type_1.getId());
            av.setAircraftType(dataset.aircraft_set_family_1_type_1);
        });
        dataset.aircraft_set_family_1_type_1_version_2 = createAircraftVersion("f1t1v2", av->{
            av.setAircraftTypeId(dataset.aircraft_set_family_1_type_1.getId());
            av.setAircraftType(dataset.aircraft_set_family_1_type_1);
        });
        dataset.aircraft_set_family_1_type_2_version_1 = createAircraftVersion("f1t2v1", av->{
            av.setAircraftTypeId(dataset.aircraft_set_family_1_type_2.getId());
            av.setAircraftType(dataset.aircraft_set_family_1_type_2);
        });
        dataset.aircraft_set_family_2_type_1_version_1 = createAircraftVersion("f2t1v1", av->{
            av.setAircraftTypeId(dataset.aircraft_set_family_2_type_1.getId());
            av.setAircraftType(dataset.aircraft_set_family_2_type_1);
        });

        dataset.aircraft_type_1.addAircraftVersion(dataset.aircraft_version_1);
        dataset.aircraft_type_2.addAircraftVersion(dataset.aircraft_version_2);
        dataset.aircraft_type_3.addAircraftVersion(dataset.aircraft_version_3);

        dataset.aircraft_family_1.addAircraftType(dataset.aircraft_type_1);
        dataset.aircraft_family_2.addAircraftType(dataset.aircraft_type_2);
        dataset.aircraft_family_3.addAircraftType(dataset.aircraft_type_3);

        // ----------------------------------------------
        // ------------- ORIGIN -------------------------
        // ----------------------------------------------
        dataset.ORIGIN_CIVP = createOrigin("CIVP", "#f00");
        dataset.ORIGIN_ISIR = createOrigin("ISIR", "#e07500");
        dataset.ORIGIN_RETEX = createOrigin("RETEX", "#009ee0");
        // ----------------------------------------------
        // ------------- STEP ---------------------------
        // ----------------------------------------------
        dataset.step_one = createStep(1, dataset.routingComponent, StepType.AUTO, Set.of());
        dataset.step_two = createStep(2, dataset.routingComponent_visual, StepType.MANUAL, Set.of());
        dataset.step_three = createStep(1, dataset.routingComponent_todo_list, StepType.MANUAL, Set.of());
        // ----------------------------------------------
        // ---------------   POST -----------------------
        // ----------------------------------------------
        dataset.post_quantity = createPost(post -> {
            post.setStep(dataset.step_one);
        }, "Quantity", "Quantité");

        dataset.post_a = createPost(post -> {
            post.setStep(dataset.step_one);
        }, "a", "a");

        dataset.post_b = createPost(post -> {
            post.setStep(dataset.step_two);
        }, "b", "b");

        dataset.post_c = createPost(post -> {
            post.setStep(dataset.step_one);
        }, "c", "c");

        dataset.post_d = createPost(post -> {
            post.setStep(dataset.step_three);
        }, "d", "d");
        // ----------------------------------------------
        // -----OPERATION FUNCTIONAL AREA  --------------
        // ----------------------------------------------
        dataset.operationFunctionalArea_one = createOperationFunctionalArea(operationFunctionalArea -> {
            operationFunctionalArea.setOperation(dataset.operation_1);
            operationFunctionalArea.setFunctionalArea(dataset.functionalArea_1);
        });
        dataset.operationFunctionalArea_two = createOperationFunctionalArea(operationFunctionalArea -> {
            operationFunctionalArea.setOperation(dataset.operation_2);
            operationFunctionalArea.setFunctionalArea(dataset.functionalArea_2);
        });
        dataset.operationFunctionalArea_three = createOperationFunctionalArea(operationFunctionalArea -> {
            operationFunctionalArea.setOperation(dataset.operation_3_todo_list);
            operationFunctionalArea.setFunctionalArea(dataset.functionalArea_partOne);
        });

        // ----------------------------------------------
        // ------------- STEP ACTIVATION ----------------
        // ----------------------------------------------
        dataset.stepActivation_one = createStepActivation(true, dataset.step_one, dataset.operationFunctionalArea_one);
        dataset.stepActivation_two = createStepActivation(false, dataset.step_two, dataset.operationFunctionalArea_two);
        dataset.stepActivation_three = createStepActivation(true, dataset.step_three, dataset.operationFunctionalArea_three);
        // ----------------------------------------------
        // ------------- REQUEST ------------------------
        // ----------------------------------------------
        dataset.request_1 = createRequest("dataset.request_1");
        dataset.request_2 = createRequest("dataset.request_2", request->{request.setStatus(EnumStatus.CREATED);});
        dataset.request_update = createRequest("Request update");
        dataset.request_save = createRequest("Request save");
        // ----------------------------------------------
        // ------------- CHILD REQUEST ------------------
        // ----------------------------------------------
        dataset.childRequest_1 = createChildRequest();
        // -------------------------------------------------------
        // -----------------CREATE PHYSICAL PART------------------
        // -------------------------------------------------------
        dataset.physicalPart_example = createPhysicalPart();
        // -------------------------------------------------------
        // ----------------- CREATE FILTERING---------------------
        // -------------------------------------------------------
        dataset.filtering_example = createFiltering();
        // -------------------------------------------------------
        // ----------------- CREATE DRT---------------------
        // -------------------------------------------------------
        dataset.drt_example = createDRT();

        dataset.websocketIdentifier_one = createWebsocketIdentifier();
    }

    public Role createRoleFromConfig(String propertyName) {
        return roleRepository.getOne(Long.valueOf(environment.getProperty(propertyName)));
    }

    public AirbusEntity createAirbusEntity(Long id, String code, String countryName) {
        AirbusEntity airbusEntity = new AirbusEntity();
        airbusEntity.setId(id);
        airbusEntity.setCode(code);
        airbusEntity.setCountryName(countryName);
        return airbusEntity;
    }

    public <T extends AbstractTranslation<E>, E extends Enum> Map<Language, Map<E, String>> toTranslationFields(Collection<T> translations) {
        Map<Language, Map<E, String>> listFields = new HashMap<>();

        translations.forEach(t -> {
            if(!listFields.containsKey(t.getLanguage())){
                listFields.put(t.getLanguage(), new HashMap<E, String>());
            }
            listFields.get(t.getLanguage()).put(t.getField(), t.getValue());
        });

        return listFields;
    }

    private Set<DamageTranslation> createTranslates(IModel entity, DamageFieldsEnum[] fields, String label) {
        Set<DamageTranslation> damageTranslationSet = new HashSet<>();
        for (var field : fields) {
            for (var lang : Language.values()){
                DamageTranslation damageTranslation = createTranslation(DamageTranslation::new, field, lang, label);
                damageTranslation.setEntity((Damage) entity);
                if(Language.FR.equals(lang)){
                    damageTranslation.setValue("Champ " + field.name() + " for " + entity.getClass().getSimpleName() + ": " + label);
                } else {
                    damageTranslation.setValue("Field " + field.name() + " for " + entity.getClass().getSimpleName() + ": " + label);
                }
                damageTranslationSet.add(damageTranslation);
            }
        }
        return damageTranslationSet;
    }

    private Set<FunctionalityDamageTranslation> createTranslates(FunctionalityDamage entity, FunctionalityDamageFieldsEnum[] fields) {
        Set<FunctionalityDamageTranslation> functionalityDamageTranslationSet = new HashSet<>();
        for (var field : fields) {
            for (var lang : Language.values()){
                FunctionalityDamageTranslation functionalityDamageTranslation =  createTranslation(FunctionalityDamageTranslation::new, field, lang, "");
                functionalityDamageTranslation.setEntity(entity);
                if(Language.FR.equals(lang)){
                    functionalityDamageTranslation.setValue("Champ " + field.name() + " pour " + entity.getClass().getSimpleName() + " numéro " +  entity.getNaturalId());
                } else {
                    functionalityDamageTranslation.setValue("Field " + field.name() + " for " + entity.getClass().getSimpleName() + " number " +  entity.getNaturalId());
                }
                functionalityDamageTranslationSet.add(functionalityDamageTranslation);
            }
        }
        return functionalityDamageTranslationSet;
    }

    @Deprecated
    public Translate createTranslate(IIdentifiedModel<Long> entity, String field, Language language, String value) {
        Translate translate = new Translate();
        translate.setClassName(entity.getClass().getSimpleName());
        translate.setEntityId(entity.getId());
        translate.setField(field);
        translate.setLanguage(language);
        translate.setValue(value);
        translate = translateRepository.save(translate);

        return translate;
    }

    public void createTranslate(IIdentifiedModel<Long> entity, Enum field, Language language, String value) {
        Translate translate = new Translate();
        translate.setClassName(entity.getClass().getSimpleName());
        translate.setEntityId(entity.getId());
        translate.setField(field.name());
        translate.setLanguage(language);
        translate.setValue(value);
        translateRepository.save(translate);
    }

    public void createTranslate(IIdentifiedVersionModel<Long> entity, Enum field, Language language, String value) {
        Translate translate = new Translate();
        translate.setClassName(entity.getClass().getSimpleName());
        translate.setEntityId(entity.getTechnicalId());
        translate.setField(field.name());
        translate.setLanguage(language);
        translate.setValue(value);
        translateRepository.save(translate);

    }

    public User createUser() {
        return createUser(dataset.airbusEntity_france, (user) -> {
        });
    }

    public User createUser(Consumer<User> modifyBeforeSave) {
        return createUser(dataset.airbusEntity_france, modifyBeforeSave);
    }

    public User createUser(AirbusEntity airbusEntity, Consumer<User> modifyBeforeSave) {
        final int number = getNext();
        User user = new User();
        user.setEmail("user-" + number + "@airbus.com");
        user.setFirstName("Firstname " + number);
        user.setLastName("Lastname " + number);
        user.setLanguage(Language.FR);
        user.setState(EnumActiveState.ACTIVE);
        user.setStaffNumber("A00000" + number);
        user.setAirbusEntityId(airbusEntity.getId());
        if (modifyBeforeSave != null) {
            modifyBeforeSave.accept(user);
        }
        user = userRepository.save(user);
        return user;
    }

    public User createUserWithRole(final String userName, final String userLastName, final RoleCode roleCode) {

        User newUserWithRequiredRole = createUser(user -> {
            user.setFirstName(userName);
            user.setLastName(userLastName);
            user.setEmail(userName + "." + userLastName + "@airbus.com");
        });
        // possibility to have a user without any role...yepa
        if (roleCode != null) {
            Role createdRole = createRole(dataset.airbusEntity_france, roleCode);
            createUserRole(createdRole, newUserWithRequiredRole);
        }
        return newUserWithRequiredRole;
    }
    public Part createPart(Set<Mpn> mpnSet) {
        return createPart(part -> {
        }, mpnSet);
    }


    public Part createPart(Consumer<Part> modifyBeforeSave, Set<Mpn> mpnSet) {
        final Long partNumber = 100000000000L;
        final Long partNumberRoot = 10000000L;
        Part part = new Part();
        part.setAta(dataset.ata_initialize);
        part.setPartDesignation(dataset.partDesignation_initialize);
        part.setPartNumber(String.valueOf(getNext() + partNumber).substring(0, 12));
        part.setPartNumberRoot(String.valueOf(getNext() + partNumberRoot).substring(0, 8));
        part.setMpnCodes(mpnSet);

        part.setVersionNumber(1L);
        part.setStatus(EnumStatus.VALIDATED);
        part.setIsLatestVersion(true);
        part.setDateUpdate(LocalDateTime.now());
        part.setCreationDate(LocalDateTime.now());

        if (modifyBeforeSave != null) {
            modifyBeforeSave.accept(part);
        }
        part = partRepository.save(part);

        return part;
    }
    public Role createRole(AirbusEntity airbusEntity, RoleCode roleCode){
        return createRole(airbusEntity, roleCode, role -> {});
    }

    public Role createRole(AirbusEntity airbusEntity, RoleCode roleCode, Consumer<Role> modifyBeforeSave) {
        Role role = new Role();
        role.setAirbusEntity(airbusEntity);
        role.setRoleCode(roleCode);
        role.setState(EnumActiveState.ACTIVE);

        if (modifyBeforeSave != null) {
            modifyBeforeSave.accept(role);
        }
        role = roleRepository.save(role);

        createTranslate(role, RoleFieldEnum.label, Language.EN, roleCode.name() + ": Role number " + role.getId());
        createTranslate(role, RoleFieldEnum.label, Language.FR, roleCode.name() + ": Role numéro " + role.getId());
        return role;
    }

    public UserFeature createUserFeature(FeatureCode featureCode, User user, EnumRightLevel rightLevel) {
        UserFeature userFeature = new UserFeature();
        userFeature.setCode(featureCode);
        userFeature.setUserId(user.getId());
        userFeature.setRightLevel(rightLevel);
        userFeature = userFeatureRepository.save(userFeature);
        return userFeature;
    }

    public UserRole createUserRole(Role role, User user) {
        return userRoleRepository.save(new UserRole(user, role));
    }

    public AirbusEntity createAirBusEntity(String code, String countryName) {
        AirbusEntity airbusEntity = new AirbusEntity();
        airbusEntity.setCode(code);
        airbusEntity.setCountryName(countryName);
        airbusEntity.setState(EnumActiveState.ACTIVE);
        airbusEntity = airbusEntityRepository.save(airbusEntity);
        return airbusEntity;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE FUNCTIONALITY -------------
     * ---------------------------------------------------- */

    public Functionality createFunctionality() {
        Functionality res = functionalityRepository.save(new Functionality());
        createTranslate(res, FunctionalityFieldsEnum.name, Language.FR, "Fonctionnalité - " + res.getId() + " (" + Language.FR + ")");
        createTranslate(res, FunctionalityFieldsEnum.name, Language.EN, "Functionality - " + res.getId() + " (" + Language.EN + ")");

        return res;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE TREATMENT -----------------
     * ---------------------------------------------------- */

    public Treatment createTreatment() {
        Treatment treatment = new Treatment();
        treatmentRepository.save(treatment);
        createTranslate(treatment, TreatmentFieldsEnum.name, Language.FR, "Traitement - " + treatment.getId() + " (" + Language.FR + ")");
        createTranslate(treatment, TreatmentFieldsEnum.name, Language.EN, "Treatment - " + treatment.getId() + " (" + Language.EN + ")");

        return treatment;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE MATERIAL ------------------
     * ---------------------------------------------------- */

    public Material createMaterial(String code) {
        Material material = new Material();
        material.setCode(code);
        material = materialRepository.save(material);
        return material;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE FUNCTIONAL AREA -----------
     * ---------------------------------------------------- */

    public FunctionalArea createFunctionalArea() {
        return createFunctionalArea(functionalArea -> {
        });
    }

    public FunctionalArea createFunctionalArea(Consumer<FunctionalArea> modifyBeforeSave) {
        FunctionalArea functionalArea = new FunctionalArea();
        functionalArea.setPart(dataset.part_example);
        functionalArea.setAreaNumber(String.valueOf(getNext()));
        functionalArea.setClassification(EnumClassification.ZC);
        functionalArea.setFunctionality(dataset.functionality_teeth);
        functionalArea.setFunctionalAreaName(dataset.functionalAreaName_OuterRingBottom);
        functionalArea.setMaterial(dataset.material_15CN6.getId());
        functionalArea.setTreatment(dataset.treatment_cadmiumPlating);
        functionalArea.setDisabled(false);

        if (modifyBeforeSave != null) {
            modifyBeforeSave.accept(functionalArea);
        }
        functionalArea = functionalAreaRepository.save(functionalArea);

        return functionalArea;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE FUNCTIONAL AREA NAME ------
     * ---------------------------------------------------- */

    public FunctionalAreaName createFunctionalAreaName() {
        return createFunctionalAreaName(functionalAreaName -> {
        }, "Functional Area Name numéro", "Functional Area Name number ");
    }

    public FunctionalAreaName createFunctionalAreaName(String translationEN, String translationFR) {
        return createFunctionalAreaName(functionalAreaName -> {
        }, translationEN, translationFR);
    }

    public FunctionalAreaName createFunctionalAreaName(Consumer<FunctionalAreaName> modifyBeforeSave, String translationEN, String translationFR) {
        FunctionalAreaName functionalAreaName = new FunctionalAreaName();
        modifyBeforeSave.accept(functionalAreaName);
        functionalAreaName = functionalAreaNameRepository.save(functionalAreaName);
        createTranslate(functionalAreaName, FunctionalityFieldsEnum.name, Language.FR, translationEN);
        createTranslate(functionalAreaName, FunctionalityFieldsEnum.name, Language.EN, translationFR);

        return functionalAreaName;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE DAMAGE --------------------
     * ---------------------------------------------------- */

    public Damage createDamage(EnumActiveState state, String name) {
        Damage damage = new Damage();
        damage.setState(state);

        damage.setVersionNumber(1L);
        damage.setStatus(EnumStatus.VALIDATED);
        damage.setIsLatestVersion(true);
        damage.setDateUpdate(LocalDateTime.now());
        damage.setCreationDate(LocalDateTime.now());
        damage.setTranslations(createTranslates(damage, DamageFieldsEnum.values(), name));

        damage = damageRepository.saveVersion(damage);

        return damage;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE FUNCTIONALITY DAMAGE ------
     * ---------------------------------------------------- */

    public FunctionalityDamage createFunctionalityDamage(Functionality functionality, Damage damage) {
        FunctionalityDamage functionalityDamage = new FunctionalityDamage();
        functionalityDamage.setFunctionality(functionality);
        functionalityDamage.setDamage(damage);

        functionalityDamage.setTranslations(new HashSet<>());
        functionalityDamage.setTranslations(createTranslates(functionalityDamage, FunctionalityDamageFieldsEnum.values()));

        functionalityDamage = functionalityDamageRepository.save(functionalityDamage);

        return functionalityDamage;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE PARTY DESIGNATION ---------
     * ---------------------------------------------------- */

    public PartDesignation createPartDesignation(final String partDesignEN, final String partDesignFR) {
        PartDesignation partDesignation = new PartDesignation();
        partDesignation = partDesignationRepository.save(partDesignation);
        if (partDesignEN == null || partDesignFR == null){
            createTranslate(partDesignation, PartDesignationFieldsEnum.designation, Language.EN, "Part designation number " + partDesignation.getId());
            createTranslate(partDesignation, PartDesignationFieldsEnum.designation, Language.FR, "Part designation numéro " + partDesignation.getId());
        }else {
            createTranslate(partDesignation, PartDesignationFieldsEnum.designation, Language.EN, partDesignEN);
            createTranslate(partDesignation, PartDesignationFieldsEnum.designation, Language.FR, partDesignFR);
        }
        return partDesignation;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE ATA -----------------------
     * ---------------------------------------------------- */

    public ATA createATA() {
        return createATA(ata -> {
        });
    }

    public ATA createATA(Consumer<ATA> modifyAta) {
        ATA ata = new ATA();
        ata.setCode(String.valueOf(getNext() * 10).substring(0, 2));
        modifyAta.accept(ata);
        ata = ataRepository.save(ata);

        return ata;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE MPN -----------------------
     * ---------------------------------------------------- */

    public Mpn createMpn() {
        return createMpn(mpn -> {
        });
    }

    public Mpn createMpn(Consumer<Mpn> modifyMpn) {
        String code = String.valueOf(getNext() * 100000).substring(0, 6) + "Z00";
        Mpn mpn = new Mpn();
        mpn.setCode(code);
        modifyMpn.accept(mpn);
        mpn = mpnRepository.save(mpn);

        return mpn;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE MEASURE UNIT --------------
     * ---------------------------------------------------- */

    public MeasureUnit createMeasureUnit() {
        MeasureUnit measureUnit = new MeasureUnit();
        measureUnit = measureUnitRepository.save(measureUnit);
        createTranslate(measureUnit, MeasureUnitsFieldsEnum.name, Language.EN, "Measure Unit name number " + measureUnit.getId());
        createTranslate(measureUnit, MeasureUnitsFieldsEnum.name, Language.FR, "Measure Unit nom numéro " + measureUnit.getId());

        return measureUnit;
    }

    public MeasureUnit createMeasureUnit(String tradFR, String tradEN) {
        MeasureUnit measureUnit = new MeasureUnit();
        measureUnit = measureUnitRepository.save(measureUnit);
        createTranslate(measureUnit, MeasureUnitsFieldsEnum.name, Language.EN, tradEN);
        createTranslate(measureUnit, MeasureUnitsFieldsEnum.name, Language.FR, tradFR);

        return measureUnit;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE STEP ----------------------
     * ---------------------------------------------------- */

    public Step createStep(int number, RoutingComponent  routingComponent, StepType stepType, Set<Post> posts) {
        return createStep(step -> {
        },routingComponent, number, stepType, posts);
    }

    public Step createStep(Consumer<Step> stepConsumer, RoutingComponent routingComponent, int number, StepType stepType, Set<Post> posts) {
        Step step = new Step();
        step.setStepNumber(number);
        step.setType(stepType);
        step.setPosts(posts);
        stepConsumer.accept(step);
        if (null != routingComponent) {
            routingComponent.addStep(step);
        }
        step.addTranslation(createTranslation(StepTranslation::new, StepFieldsEnum.name, Language.EN, "Step Name"));
        step.addTranslation(createTranslation(StepTranslation::new, StepFieldsEnum.name, Language.FR, "Nom de  l'étape"));
        step.addTranslation(createTranslation(StepTranslation::new, StepFieldsEnum.information, Language.EN, "information Step"));
        step.addTranslation(createTranslation(StepTranslation::new, StepFieldsEnum.information, Language.FR, "informations de l'étape"));

        entityManager.persist(step);


        return step;
    }

    public Step createStep(Consumer<Step> stepConsumer) {
        Step step = new Step();
        step.setRoutingComponent(dataset.routingComponent);
        step.setStepNumber(1);
        step.setType(StepType.MANUAL);
        step.setFiles(new HashSet<>());
        stepConsumer.accept(step);

        step.addTranslation(createTranslation(StepTranslation::new, StepFieldsEnum.name, Language.EN, "Step Name"));
        step.addTranslation(createTranslation(StepTranslation::new, StepFieldsEnum.name, Language.FR, "Nom de  l'étape"));
        step.addTranslation(createTranslation(StepTranslation::new, StepFieldsEnum.information, Language.EN, "information Step"));
        step.addTranslation(createTranslation(StepTranslation::new, StepFieldsEnum.information, Language.FR, "informations de l'étape"));

        entityManager.persist(step);

        return step;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE STEP ACTIVATION -----------
     * ---------------------------------------------------- */

    public StepActivation createStepActivation(boolean activated, Step step, OperationFunctionalArea operationFunctionalArea) {
        return createStepActivation(stepActivation -> {
        }, activated, step, operationFunctionalArea);
    }

    public StepActivation createStepActivation(Consumer<StepActivation> modifyStepActivation, boolean activated,
                                               Step step, OperationFunctionalArea operationFunctionalArea) {
        StepActivation stepActivation = new StepActivation();
        stepActivation.setActivated(activated);
        stepActivation.setStep(step);
        stepActivation.setOperationFunctionalArea(operationFunctionalArea);
        modifyStepActivation.accept(stepActivation);
        stepActivation = stepActivationRepository.save(stepActivation);
        return stepActivation;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE POST  ---------------------
     * ---------------------------------------------------- */

    public Post createPost(String designationFR, String designationEN) {
        return createPost(post -> {
        }, designationFR ,designationEN );
    }

    public Post createPost(Consumer<Post> modifyPost, String designationFR, String designationEN) {
        Post post = new Post();
        post.setMeasureUnit(dataset.measureUnit_mm);
        post.setMeasureUnitId(dataset.measureUnit_mm.getId());
        post.setStep(dataset.step_one);
        modifyPost.accept(post);

        post.addTranslation(createTranslation(PostTranslation::new, PostFieldsEnum.designation, Language.EN, designationEN));
        post.addTranslation(createTranslation(PostTranslation::new, PostFieldsEnum.designation, Language.FR, designationFR));
        entityManager.persist(post);

        return post;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE OPERATION TYPE  -----------
     * ---------------------------------------------------- */

    public OperationType createOperationType() {
        return createOperationType(operationType -> {
        });
    }

    public OperationType createOperationType(Consumer<OperationType> modifyOperationType) {
        OperationType operationType = new OperationType();
        operationType.setTemplate("visual");
        modifyOperationType.accept(operationType);
        operationType = operationTypeRepository.save(operationType);
        createTranslate(operationType, OperationTypeFieldsEnum.name, Language.EN, "Operation Type name number " + operationType.getId());
        createTranslate(operationType, OperationTypeFieldsEnum.name, Language.FR, "Nom du type post_d'opération numéro " + operationType.getId());

        return operationType;
    }

    public OperationType createOperationType(Consumer<OperationType> modifyOperationType, String nameFR, String nameEN) {
        OperationType operationType = new OperationType();
        operationType.setTemplate("visual");
        modifyOperationType.accept(operationType);
        operationType = operationTypeRepository.save(operationType);
        createTranslate(operationType, OperationTypeFieldsEnum.name, Language.EN, nameEN);
        createTranslate(operationType, OperationTypeFieldsEnum.name, Language.FR, nameFR);

        return operationType;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE INSPECTION ----------------
     * ---------------------------------------------------- */

    public Inspection createInspection(String nameEN, String nameFR) {
        return createInspection(inspection -> {
        }, nameEN, nameFR);
    }

    public Inspection createInspection(Consumer<Inspection> modifyInspection, String nameEN, String nameFR) {
        Inspection inspection = new Inspection();
        inspection.setValue("internal");
        modifyInspection.accept(inspection);
        inspection = inspectionRepository.save(inspection);
        createTranslate(inspection, InspectionEnumFields.name, Language.FR, nameFR);
        createTranslate(inspection, InspectionEnumFields.name, Language.EN, nameEN);

        return inspection;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE ROUTING COMPONENT ---------
     * ---------------------------------------------------- */

    public RoutingComponent createRoutingComponent() {
        return createRoutingComponent(routingComponent -> {
        });
    }

    public RoutingComponent createRoutingComponent(Consumer<RoutingComponent> modifyRoutingComponent) {
        RoutingComponent routingComponent = new RoutingComponent();
        routingComponent.setDamageId(dataset.damage_corrosion.getNaturalId());
        routingComponent.setOperationType(dataset.operationType_visual);
        routingComponent.setFunctionality(dataset.functionality_bearingRaces);
        routingComponent.setInspection(dataset.inspection_internal);
        modifyRoutingComponent.accept(routingComponent);
        routingComponent = routingComponentRepository.save(routingComponent);
        createTranslate(routingComponent, RoutingComponentFieldsEnum.name, Language.EN, "Routing Component name : " + routingComponent.getNaturalId());

        return routingComponent;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE TODOLISTNAME --------------
     * ---------------------------------------------------- */

    public TodoListName createTodoListName() {
        return createTodoListName(todoListName -> {
        });
    }

    public TodoListName createTodoListName(Consumer<TodoListName> modifyTask) {
        TodoListName todoListName = new TodoListName();
        modifyTask.accept(todoListName);
        todoListName = todoListNameRepository.save(todoListName);
        createTranslate(todoListName, TodoListNameFieldsEnum.name, Language.FR, "TodoListName nom numéro " + todoListName.getId());
        createTranslate(todoListName, TodoListNameFieldsEnum.name, Language.EN, "TodoListName name number " + todoListName.getId());

        return todoListName;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE TODOLIST ------------------
     * ---------------------------------------------------- */

    public TodoList createTodoList() {
        return createTodoList(todoList -> {
        });
    }

    public TodoList createTodoList(Consumer<TodoList> modifyTodoList) {
        TodoList todoList = new TodoList();
        todoList.setTodoListNameId(dataset.todoListName_1.getId());
        todoList.setTodoListName(dataset.todoListName_1);
        todoList.setInspection(dataset.inspection_external);
        todoList.setOperationType(dataset.operationType_preliminary);
        modifyTodoList.accept(todoList);
        todoList = todoListRepository.save(todoList);

        createTranslate(todoList, TodoListFieldsEnum.informations, Language.FR, "TodoList informations numéro " + todoList.getNaturalId());
        createTranslate(todoList, TodoListFieldsEnum.informations, Language.EN, "TodoList informations number " + todoList.getNaturalId());


        return todoList;
    }

    /* ----------------------------------------------------
     * ------- CREATE ROUTING COMPONENT INDEX -------------
     * ---------------------------------------------------- */

    public RoutingComponentIndex createRoutingComponentIndex(Long routingComponentId, Long todoListId) {
        return createRoutingComponentIndex(routingComponentId, todoListId, routingComponentIndex -> {
        });
    }

    public RoutingComponentIndex createRoutingComponentIndex(Long routingComponentId, Long todoListId, Consumer<RoutingComponentIndex> modifyRoutingComponentIndex) {
        RoutingComponentIndex routingComponentIndex = new RoutingComponentIndex();

        routingComponentIndex.setVersionNumber(1L);
        routingComponentIndex.setStatus(EnumStatus.VALIDATED);
        routingComponentIndex.setIsLatestVersion(true);
        routingComponentIndex.setDateUpdate(LocalDateTime.now());
        routingComponentIndex.setCreationDate(LocalDateTime.now());

        if (null != routingComponentId) {
            Optional<RoutingComponent> routingComponentOpt = routingComponentRepository.findById(routingComponentId);
            if (routingComponentOpt.isPresent()) {
                routingComponentIndex.setRoutingComponent(routingComponentOpt.get());
                routingComponentIndex.getRoutingComponent().setRoutingComponentIndex(routingComponentIndex);
            }
        } else {
            Optional<TodoList> todoListOptional = todoListRepository.findById(todoListId);
            if (todoListOptional.isPresent()) {
                routingComponentIndex.setTodoList(todoListOptional.get());
                routingComponentIndex.getTodoList().setRoutingComponentIndex(routingComponentIndex);
            }
        }

        modifyRoutingComponentIndex.accept(routingComponentIndex);
        routingComponentIndex = routingComponentIndexRepository.saveVersion(routingComponentIndex);

        createTranslate(routingComponentIndex, RoutingComponentFieldsEnum.name, Language.EN, "Routing Component name number " + routingComponentIndex.getNaturalId());
        createTranslate(routingComponentIndex, RoutingComponentFieldsEnum.name, Language.FR, "Composant gamme nom numéro" + routingComponentIndex.getNaturalId());

        return routingComponentIndex;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE ROUTING -------------------
     * ---------------------------------------------------- */

    public Routing createRouting(Part part) {
        return createRouting(routing -> {
        }, part);
    }

    public Routing createRouting(Consumer<Routing> modifyRouting, Part part) {
        Routing routing = new Routing();
        routing.setVersionNumber(1L);
        routing.setIsLatestVersion(true);
        routing.setCreationDate(LocalDateTime.now());
        routing.setStatus(EnumStatus.VALIDATED);
        routing.setCreator(dataset.user_superAdmin);
        routing.setPart(reference(part));
        routing.addTranslation(createTranslation(RoutingTranslation::new, RoutingFieldsEnum.name, Language.FR, "Gamme informations nom " + routing.getNaturalId() + " (" + Language.FR + ")"));
        routing.addTranslation(createTranslation(RoutingTranslation::new, RoutingFieldsEnum.name, Language.EN, "Routing  informations name" + routing.getNaturalId() + " (" + Language.EN + ")"));
        modifyRouting.accept(routing);
        routing = routingRepository.save(routing);

        return routing;
    }

    public <T extends AbstractTranslation<E>, E extends Enum> T createTranslation(Supplier<T> supplier, E field, Language language, String value) {
        T translation = supplier.get();
        translation.setLanguage(language);
        translation.setField(field);
        translation.setValue(value);
        return translation;
    }
    /* ----------------------------------------------------
     * ----------------- CREATE OPERATION -----------------
     * ---------------------------------------------------- */

    public Operation createOperation(Integer orderNumber) {
        return createOperation(orderNumber, operation -> {
        });
    }

    public Operation createOperation(Integer orderNumber, Consumer<Operation> modifyOperation) {
        Operation operation = new Operation();
        operation.setOrderNumber(orderNumber);
        operation.setOperationType(dataset.operationType_dimensional);
        operation.setRouting(dataset.routing_1);
        modifyOperation.accept(operation);
        operation = operationRepository.save(operation);

        return operation;
    }

    /* ----------------------------------------------------
     * ----------- CREATE OPERATION FUNCTIONAL AREA -------
     * ---------------------------------------------------- */

    public OperationFunctionalArea createOperationFunctionalArea() {
        return createOperationFunctionalArea(ofa -> {
        });
    }

    public OperationFunctionalArea createOperationFunctionalArea(Consumer<OperationFunctionalArea> modifyOperationFunctionalArea) {
        OperationFunctionalArea operationFunctionalArea = new OperationFunctionalArea();
        operationFunctionalArea.setOperation(dataset.operation_1);
        operationFunctionalArea.setFunctionalArea(dataset.functionalArea_1);
        modifyOperationFunctionalArea.accept(operationFunctionalArea);
        operationFunctionalArea = operationFunctionalAreaRepository.save(operationFunctionalArea);

        return operationFunctionalArea;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE OPERATION POST -----------
     * ---------------------------------------------------- */

    public RoutingFunctionalAreaPost createRoutingFunctionalAreaPost() {
        return createRoutingFunctionalAreaPost(opPost -> {
        });
    }

    public RoutingFunctionalAreaPost createRoutingFunctionalAreaPost(Consumer<RoutingFunctionalAreaPost> operationPostConsumer) {
        RoutingFunctionalAreaPost routingFunctionalAreaPost = new RoutingFunctionalAreaPost();
        routingFunctionalAreaPost.setStepActivation(dataset.stepActivation_one);
        routingFunctionalAreaPost.setPost(dataset.post_a);
        routingFunctionalAreaPost.setThreshold(5.0f);
        operationPostConsumer.accept(routingFunctionalAreaPost);
        routingFunctionalAreaPost = routingFunctionalAreaPostRepository.save(routingFunctionalAreaPost);
        return routingFunctionalAreaPost;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE MEDIA ---------------------
     * ---------------------------------------------------- */

    public Media createMedia() {
        return createMedia(media -> {
        });
    }

    public Media createMedia(Consumer<Media> mediaConsumer) {
        Media media = new Media("image.jpg");
        media.setCreatedAt(LocalDateTime.now());
        mediaConsumer.accept(media);
        return mediaRepository.save(media);
    }

    /* ----------------------------------------------------
     * ----------------- CREATE TEMPORARY MEDIA -----------
     * ---------------------------------------------------- */

    public MediaTmp createTemporaryMedia() {
        return createTemporaryMedia(media -> {
        });
    }

    public MediaTmp createTemporaryMedia(Consumer<MediaTmp> mediaConsumer) {
        MediaTmp mediaTmp = new MediaTmp();
        mediaTmp.setFilename("image.jpg");
        mediaTmp.setCreatedAt(LocalDateTime.now());
        mediaConsumer.accept(mediaTmp);
        mediaTemporaryRepository.save(mediaTmp);
        return mediaTmp;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE MISSION TYPE --------------
     * ---------------------------------------------------- */

    public MissionType createMissionType() {
        MissionType mission = new MissionType();
        mission = missionTypeRepository.save(mission);
        createTranslate(mission, MissionTypeFieldsEnum.name, Language.EN, "Mission type number " + mission.getId());
        createTranslate(mission, MissionTypeFieldsEnum.name, Language.FR, "Mission type numéro " + mission.getId());

        return mission;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE ENVIRONMENT ---------------
     * ---------------------------------------------------- */

    public Environment createEnvironment() {
        Environment environment = new Environment();
        environment = environmentRepository.save(environment);
        createTranslate(environment, EnvironmentFieldsEnum.name, Language.EN, "Environment number " + environment.getId());
        createTranslate(environment, EnvironmentFieldsEnum.name, Language.FR, "Environnement numéro " + environment.getId());

        return environment;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE CLIENT --------------------
     * ---------------------------------------------------- */

    public Client createClient(String name) {
        Client client = new Client();
        client.setName(name);
        client = clientRepository.save(client);

        return client;
    }

    /* ----------------------------------------------------
     * ----------------- CREATE AIRCRAFT FAMILY------------------
     * ---------------------------------------------------- */

    public AircraftFamily createAircraftFamily(String name) {
        AircraftFamily aircraftFamily = new AircraftFamily();
        aircraftFamily.setName(name);
        aircraftFamily.addAircraftType(dataset.aircraft_type_1);
        aircraftFamily.addAircraftType(dataset.aircraft_type_2);
        aircraftFamily = aircraftFamilyRepository.save(aircraftFamily);

        return aircraftFamily;
    }


    public AircraftFamily createAircraftFamily() {
        return createAircraftFamily((Consumer<AircraftFamily>)null);
    }


    public AircraftFamily createAircraftFamily(Consumer<AircraftFamily> modifyBeforeSave) {
        AircraftFamily aircraftFamily = new AircraftFamily();
        aircraftFamily.setName("Aircraft family "+getNext());
        if(modifyBeforeSave != null) {
            modifyBeforeSave.accept(aircraftFamily);
        }
        return aircraftFamilyRepository.saveAndFlush(aircraftFamily);
    }

    /* ----------------------------------------------------
     * ----------------- CREATE AIRCRAFT TYPE------------------
     * ---------------------------------------------------- */
    public AircraftType createAircraftType(String name) {
        return createAircraftType(name, aircraftType -> {
        });
    }

    public AircraftType createAircraftType(String name, Consumer<AircraftType> modifyAircraftType) {
        AircraftType aircraftType = new AircraftType();
        aircraftType.setName(name);
        aircraftType.setAircraftFamily(dataset.aircraft_family_1);
        aircraftType.setAircraftFamilyId(dataset.aircraft_family_1.getId());
        modifyAircraftType.accept(aircraftType);
        aircraftType = aircraftTypeRepository.save(aircraftType);

        return aircraftType;
    }
    /* ----------------------------------------------------
     * ----------------- CREATE AIRCRAFT VERSION------------------
     * ---------------------------------------------------- */
    public AircraftVersion createAircraftVersion(String name) {
        return createAircraftVersion(name, aircraftVersion -> {
        });
    }

    public AircraftVersion createAircraftVersion(String name,Consumer<AircraftVersion> modifyAircraftVersion) {
        AircraftVersion aircraftVersion = new AircraftVersion();
        aircraftVersion.setName(name);
        aircraftVersion.setAircraftType(dataset.aircraft_type_1);
        aircraftVersion.setAircraftTypeId(dataset.aircraft_type_1.getId());
        modifyAircraftVersion.accept(aircraftVersion);
        aircraftVersion = aircraftVersionRepository.save(aircraftVersion);

        return aircraftVersion;
    }
    /* ----------------------------------------------------
     * ----------------- CREATE REQUEST -------------------
     * ---------------------------------------------------- */

    public Request createRequest(String name) {
        return createRequest(name, request -> {
        });
    }

    public Request createRequest(String name, Consumer<Request> modifyRequest) {
        int next = getNext();
        Request request = new Request();
        request.setRequester(dataset.user_superAdmin);
        request.setStatus(EnumStatus.IN_PROGRESS);
        request.setReference("RQ_" + next);
        request.setCreationDate(LocalDateTime.now());
        request.setAirbusEntity(dataset.airbusEntity_france);
        request.setAta(dataset.ata_1);
        request.setRequester(dataset.user_superAdmin);
        request.setOrigin(dataset.ORIGIN_CIVP);
        request.setValidator(dataset.user_simpleUser2);
        request.setDueDate(LocalDate.now().plusDays(3));
        request.setEnvironment(dataset.environment_1);
        request.setAircraftFamily(dataset.aircraft_family_1);
        request.addAircraftType(dataset.aircraft_type_1);
        request.addAircraftVersion(dataset.aircraft_version_1);
        request.setMissionType(dataset.mission_1);
        request.addClient(dataset.client_1);
        request.addOperator(dataset.user_partReader);
        request.setOetp("Oetp of request "+next);
        request.setOriginComment("Origin comment of request "+next);
        request.setSpecComment("Spec comment of request "+next);
        request.addOriginMedia(dataset.media_1);
        request.addSpecMedia(dataset.media_1);
        request.setTechnicalResponsibles(new HashSet<>(Arrays.asList(dataset.user_partReader)));
        request.setVersion(1L);
        modifyRequest.accept(request);
        request = requestRepository.save(request);

        createTranslate(request, RequestFieldsEnum.name, Language.FR, name + request.getId() + " (" + Language.FR + ")");
        createTranslate(request, RequestFieldsEnum.name, Language.EN, name + request.getId() + " (" + Language.EN + ")");
        createTranslate(request, RequestFieldsEnum.description, Language.FR, "Description en francais pour " + name + request.getId() + " (" + Language.FR + ")");
        createTranslate(request, RequestFieldsEnum.description, Language.EN, "Description in english for " + name + request.getId() + " (" + Language.EN + ")");

        return request;
    }




     /* ----------------------------------------------------
     * ----------------- CREATE PHYSICAL PART --------------
     * ---------------------------------------------------- */
     public PhysicalPart createPhysicalPart(final ChildRequest childRequest, final String value) {
         return createPhysicalPart(pp -> {
             pp.setChildRequest(childRequest);
             pp.setSerialNumber(value);
        });
    }

    public PhysicalPart createPhysicalPart(final Consumer<PhysicalPart> modify) {
        PhysicalPart physicalPart = new PhysicalPart();
        physicalPart.setSerialNumber(getNextCode(10));
        physicalPart.setPart(dataset.part_example);
        modify.accept(physicalPart);
        physicalPart = physicalPartRepository.save(physicalPart);
        return physicalPart;
    }

    public PhysicalPart createPhysicalPart() {
        return createPhysicalPart(pp->{});
    }

    /* ----------------------------------------------------
     * ----------------- CREATE FILTERING -----------------
     * ---------------------------------------------------- */

    public Filtering createFiltering(final Consumer<Filtering> modify) {
        Filtering f = new Filtering();
        f.setPhysicalPart(dataset.physicalPart_example);
        f.setAircraftSerialNumber(SERIAL_NUMBER_AIRCRAFT);
        f.setAircraftFamily(dataset.aircraft_family_1);
        f.setAircraftType(dataset.aircraft_type_1);
        f.setAircraftVersion(dataset.aircraft_version_1);
        f.setStatus(EnumStatus.CREATED);
        modify.accept(f);

        PhysicalPart pp = f.getPhysicalPart();
        if (pp != null && pp.getEquipmentNumber() == null) {
            pp.setEquipmentNumber("RND" + getNextCode(3));
            physicalPartRepository.saveAndFlush(pp);
        }

        return filteringRepository.saveAndFlush(f);
    }

    public Filtering createFiltering() {
        return createFiltering(f -> {
        });
    }

    /* ----------------------------------------------------
     * ----------------- CREATE ORIGIN --------------------
     * ---------------------------------------------------- */

    public Origin createOrigin(String label, String color) {
        Origin item = new Origin();
        item.setName(label);
        item.setColor(color);
        return originRepository.save(item);
    }

    /* ----------------------------------------------------
     * --------------- CREATE CHILD REQUEST ---------------
     * ---------------------------------------------------- */

    public ChildRequest createChildRequest() {
        return createChildRequest(request -> {
        });
    }

    @Transactional
    public ChildRequest createChildRequest( Consumer<ChildRequest> modifyRequest) {
        ChildRequest childRequest = new ChildRequest();
        childRequest.setStatus(EnumStatus.IN_PROGRESS);
        childRequest.setVersion(1L);
        childRequest.setEnvironment(environmentRepository.getOne(dataset.environment_1.getId()));
        childRequest.setDrtToInspect(55L);
        childRequest.setModulation(25);
        childRequest.setParentRequest(requestRepository.getOne(dataset.request_1.getId()));
        childRequest.addMedia(reference(dataset.media_1));
        childRequest.setRoutingNaturalId(dataset.routing_1.getNaturalId());

        childRequest.setAircraftFamily(dataset.aircraft_family_1);
        childRequest.getAircraftTypes().add(dataset.aircraft_type_1);
        childRequest.getAircraftVersions().add(dataset.aircraft_version_1);
        MissionType msType = missionTypeRepository.saveAndFlush(new MissionType());
        childRequest.setMissionType(msType);
        childRequest.setStatus(EnumStatus.CREATED);
        childRequest.addClient(clientRepository.getOne(dataset.client_1.getId()));
        modifyRequest.accept(childRequest);
        childRequest = childRequestRepository.saveAndFlush(childRequest);
        childRequest.addPhysicalPart(createPhysicalPart(childRequest, SERIAL_NUMBER_PHYSICAL_PART));

        if (childRequest.getParentRequest() != null) {
            Request parentRequest = childRequest.getParentRequest();
            parentRequest.addChildRequest(childRequest);
            requestRepository.save(parentRequest);
        }

        return childRequest;
    }

    /* --------------------------------------------------------------
     * ----------------- CREATE DRT               -------------------
     * -------------------------------------------------------------- */
    public Drt createDRT(final Consumer<Drt> modifyDrt) {
        Drt drt = new Drt();
        drt.setChildRequest(childRequestRepository.getOne(dataset.childRequest_1.getId()));
        modifyDrt.accept(drt);
        return drtRepository.saveAndFlush(drt);
    }

    public Drt createDRT() {
        return createDRT(drt -> {
        });
    }

    /* --------------------------------------------------------------
     * ----------------- CREATE ControlRoutingComponent-------------------
     * -------------------------------------------------------------- */
    public ControlRoutingComponent createControlRoutingComponent(final Consumer<ControlRoutingComponent> modifyControl) {
        ControlRoutingComponent controlRoutingComponent = new ControlRoutingComponent();
        controlRoutingComponent.setValue(4F);
        controlRoutingComponent.setDrt(dataset.drt_example);
        modifyControl.accept(controlRoutingComponent);
        return controlRoutingComponentRepository.saveAndFlush(controlRoutingComponent);
    }

    /* --------------------------------------------------------------
     * ----------------- CREATE ControlControlTodoList-------------------
     * -------------------------------------------------------------- */
    public ControlTodoList createControlControlTodoList(final Consumer<ControlTodoList> modifyControl) {
        ControlTodoList controlTodoList = new ControlTodoList();
        controlTodoList.setValue(EnumTodoListValue.OK);
        controlTodoList.setDrt(dataset.drt_example);
        modifyControl.accept(controlTodoList);
        return controlTodoListRepository.saveAndFlush(controlTodoList);
    }

    /* --------------------------------------------------------------
     * ----------------- CREATE ControlControlTodoList-------------------
     * -------------------------------------------------------------- */
    public DrtPictures createDrtPictures(Drt drt, StepActivation stepActivation) {
        return createDrtPictures(drt1 -> {
            drt1.setDrt(drt);
            drt1.setStepActivation(stepActivation);
        });
    }

    /* --------------------------------------------------------------
     * ----------------- CREATE ControlControlTodoList-------------------
     * -------------------------------------------------------------- */
    public DrtPictures createDrtPictures(final Consumer<DrtPictures> modifyControl) {
        DrtPictures drtPictures = new DrtPictures();
        drtPictures.setActivated(true);
        drtPictures.setMedias(Set.of(createMedia()));
        modifyControl.accept(drtPictures);
        return drtPicturesRepository.saveAndFlush(drtPictures);
    }

    /* --------------------------------------------------------------
     * ----------------- CREATE ControlControlVisual-------------------
     * -------------------------------------------------------------- */
    public ControlVisual createControlControlVisual(final Consumer<ControlVisual> modifyControl) {
        ControlVisual controlVisual = new ControlVisual();
        controlVisual.setValue(Boolean.TRUE);
        controlVisual.setDrt(dataset.drt_example);
        modifyControl.accept(controlVisual);
        return controlVisualRepository.saveAndFlush(controlVisual);
    }

    public DrtOperationStatusFunctionalArea createDrtOperationStatusFunctionalArea(Drt drt, OperationFunctionalArea ofa, EnumStatus status) {
        DrtOperationStatusFunctionalArea drtOFA = new DrtOperationStatusFunctionalArea();
        drtOFA.setDrt(drt);
        drtOFA.setOperationFunctionalArea(ofa);
        drtOFA.setStatus(status);

        return drtOperationStatusRepository.saveAndFlush(drtOFA);
    }

    public DrtOperationStatusFunctionalArea createDrtOperationStatusFunctionalArea(Drt drt, OperationFunctionalArea ofa) {
        return createDrtOperationStatusFunctionalArea(drt, ofa, EnumStatus.IN_PROGRESS);
    }

    public DrtOperationStatusTodoList createDrtOperationStatusTodoList(Drt drt, Operation operation, TodoList todoList, EnumStatus status) {
        DrtOperationStatusTodoList drtOTL = new DrtOperationStatusTodoList();
        drtOTL.setDrt(drt);
        drtOTL.setOperation(operation);
        drtOTL.setTodoList(todoList);

        drtOTL.setStatus(status);

        return drtOperationStatusRepository.saveAndFlush(drtOTL);
    }

    public DrtOperationStatusTodoList createDrtOperationStatusTodoList(Drt drt, Operation operation, TodoList todoList) {
        return createDrtOperationStatusTodoList(drt, operation, todoList, EnumStatus.IN_PROGRESS);
    }

    public QcheckRoutingComponent createQcheckRoutingComponent(final Consumer<QcheckRoutingComponent> modifyControl) {
        QcheckRoutingComponent qcheckRoutingComponent = new QcheckRoutingComponent();
        qcheckRoutingComponent.setRoutingComponentIndex(dataset.routingComponentIndex_1);
        qcheckRoutingComponent.setOperationFunctionalArea(dataset.operationFunctionalArea_one);
        qcheckRoutingComponent.setValue(Boolean.TRUE);
        qcheckRoutingComponent.setDrt(dataset.drt_example);
        modifyControl.accept(qcheckRoutingComponent);
        return qcheckRoutingComponentRepository.save(qcheckRoutingComponent);

    }

    /* --------------------------------------------------------------
     * ----------------- CREATE WEBSOCKET IDENTIFIED CLIENTS --------
     * -------------------------------------------------------------- */
    public WebsocketIdentifier createWebsocketIdentifier(final Consumer<WebsocketIdentifier> modify) {
        WebsocketIdentifier websocketIdentifier = new WebsocketIdentifier();
        websocketIdentifier.setConnected(true);
        websocketIdentifier.setWebsocketSessionId("dsdsj45-fd4fd5re-45rerer-fdfd45fdf");
        websocketIdentifier.setUserId(dataset.user_superAdmin.getId());
        modify.accept(websocketIdentifier);
        return websocketIdentifierRepository.save(websocketIdentifier);

    }

    public WebsocketIdentifier createWebsocketIdentifier() {
        return createWebsocketIdentifier(websocketIdentifier -> {});
    }
}
