package  com.airbus.retex.service.drt;


import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.drt.DrtDto;
import com.airbus.retex.business.dto.drt.DrtFilteringDto;
import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.part.PartDesignation;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.drt.DrtRepository;
import com.airbus.retex.persistence.filtering.FilteringRepository;
import com.airbus.retex.persistence.part.PartDesignationRepository;
import com.airbus.retex.persistence.request.RequestRepository;
import com.airbus.retex.service.admin.IUserService;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DrtGetListServiceIT extends AbstractServiceIT {
    private static final String API_DRTS = "/api/drts";


    @Autowired
    private DrtRepository drtRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ChildRequestRepository childrequestRepository;

    @Autowired
    private IDrtService drtService;

    @Autowired
    private FilteringRepository filteringRepository;

    @Autowired
    private PartDesignationRepository partDesignationRepository;

    @Autowired
    private IUserService userService;


    private Filtering filtering1_1;
    private Filtering filtering1_2;
    private Filtering filtering2_1;
    private Filtering filtering2_2;

    private Drt drt1_1;// associated to filtering1_1
    private Drt drt1_2;// associated to filtering1_2
    private Drt drt2_1;// associated to filtering2_1
    private Drt drt2_2;// associated to filtering2_2

    private Request parentRequest1;
    private Request parentRequest2;
    private ChildRequest childRequest1; // associated to parentRequest1
    private ChildRequest childRequest2; // associated to parentRequest2
    private PhysicalPart physicalPart1_1; // associated to filtering1_1
    private PhysicalPart physicalPart1_2; // associated to filtering1_2
    private PhysicalPart physicalPart2_1; // associated to filtering2_1
    private PhysicalPart physicalPart2_2; // associated to filtering2_1

    private Part part1;
    private Part part2;

    private PartDesignation partDesignation1;
    private PartDesignation partDesignation2;
    private static String PART_DESIGNATION1_EN = "partDesignation1_EN";
    private static String PART_DESIGNATION2_EN = "partDesignation2_EN";
    private static String PART_NUMBER1 = "110000000011";
    private static String PART_NUMBER2 = "110000000022";
    public static final String SERIAL_NUMBER_PP_11 = "01111";
    public static final String SERIAL_NUMBER_PP_12 = "01112";
    public static final String SERIAL_NUMBER_PP_21 = "01122";
    public static final String SERIAL_NUMBER_PP_22 = "02223";
    // Operators of request 1
    private User operator1_1;
    private User operator1_2;
    // Operators of request 2
    private User operator2_1;
    private User operator2_2;
    // Technical Responsibles of request 1
    private User technicalResponsible1_1;
    private User technicalResponsible1_2;
    // Technical Responsibles of request 2
    private User technicalResponsible2_1;
    private  User technicalResponsible2_2;

    @BeforeEach
    private void before() {
        operator1_1 = createUserWithRole("operator", "1_1", RoleCode.INTERNAL_OPERATOR);
        operator1_2 = createUserWithRole("operator", "1_2", RoleCode.INTERNAL_OPERATOR);
        operator2_1 = createUserWithRole("operator", "2_1", RoleCode.INTERNAL_OPERATOR);
        operator2_2 = createUserWithRole("operator", "2_2", RoleCode.INTERNAL_OPERATOR);

        technicalResponsible1_1 = createUserWithRole("technicalResponsible", "1_1", RoleCode.TECHNICAL_RESPONSIBLE);
        technicalResponsible1_2 = createUserWithRole("technicalResponsible", "1_2", RoleCode.TECHNICAL_RESPONSIBLE);
        technicalResponsible2_1 = createUserWithRole("technicalResponsible", "2_1", RoleCode.TECHNICAL_RESPONSIBLE);
        technicalResponsible2_2 = createUserWithRole("technicalResponsible", "2_2", RoleCode.TECHNICAL_RESPONSIBLE);




        parentRequest1 = dataSetInitializer.createRequest("request 1" , r-> {
            r.addOperator(operator1_1);
            r.addOperator(operator1_2);
            r.addTechnicalResponsible(technicalResponsible1_1);
            r.addTechnicalResponsible(technicalResponsible1_2);
        });
        parentRequest2 = dataSetInitializer.createRequest("request 2" , r-> {
            r.addOperator(operator2_1);
            r.addOperator(operator2_2);
            r.addTechnicalResponsible(technicalResponsible2_1);
            r.addTechnicalResponsible(technicalResponsible2_2);
        });
        partDesignation1 = dataSetInitializer.createPartDesignation(PART_DESIGNATION1_EN, "partDesignation1_FR");
        partDesignation2 = dataSetInitializer.createPartDesignation(PART_DESIGNATION2_EN, "partDesignation2_FR");
        part1 = dataSetInitializer.createPart(part1 -> {
                    part1.setPartNumber(PART_NUMBER1);
                    part1.setPartDesignation(partDesignation1);
                }
                ,null);
        part2 = dataSetInitializer.createPart(part1 -> {
                    part1.setPartNumber(PART_NUMBER2);
                    part1.setPartDesignation(partDesignation1);
                }
                ,null);

        Routing routing = dataSetInitializer.createRouting(part1);
        childRequest1 = dataSetInitializer.createChildRequest(cr -> {
            cr.setParentRequest(parentRequest1);
            cr.setRouting(routing);
            cr.setRoutingNaturalId(routing.getNaturalId());
            cr.setStatus(EnumStatus.CREATED);
        });


        childRequest2 = dataSetInitializer.createChildRequest(cr -> {
            cr.setParentRequest(parentRequest2);
            cr.setRouting(routing);
            cr.setRoutingNaturalId(routing.getNaturalId());
            cr.setStatus(EnumStatus.CREATED);
        });

        physicalPart1_1 = dataSetInitializer.createPhysicalPart(physicalPart1 -> {
            physicalPart1.setPart(part1);
            physicalPart1.setChildRequest(childRequest1);
            physicalPart1.setSerialNumber(SERIAL_NUMBER_PP_11);

        });
        physicalPart1_2 = dataSetInitializer.createPhysicalPart(physicalPart1 -> {
            physicalPart1.setPart(part1);
            physicalPart1.setChildRequest(childRequest1);
            physicalPart1.setSerialNumber(SERIAL_NUMBER_PP_12);

        });

        childRequest1.addPhysicalPart(physicalPart1_1);
        childRequest1.addPhysicalPart(physicalPart1_2);

        filtering1_1 = dataSetInitializer.createFiltering(f -> {
            f.setPhysicalPart(physicalPart1_1);
            f.getPhysicalPart().setChildRequest(childRequest1);
        });
        filtering1_2 = dataSetInitializer.createFiltering(f -> {
            f.setPhysicalPart(physicalPart1_2);
            f.getPhysicalPart().setChildRequest(childRequest1);
        });

        drt1_1 = dataSetInitializer.createDRT(drt-> {
            drt.setFiltering(filtering1_1);
            drt.setStatus(EnumStatus.VALIDATED);
            drt.setIntegrationDate(LocalDate.now());
        });
        drt1_2 = dataSetInitializer.createDRT(drt-> {
            drt.setFiltering(filtering1_2);
            drt.setStatus(EnumStatus.VALIDATED);
            drt.setIntegrationDate(LocalDate.now().plusDays(1));
        });
        filtering1_1.setDrt(drt1_1);
        filtering1_2.setDrt(drt1_2);

        physicalPart2_1 = dataSetInitializer.createPhysicalPart(physicalPart -> {
            physicalPart.setPart(part2);
            physicalPart.setChildRequest(childRequest2);
            physicalPart.setSerialNumber(SERIAL_NUMBER_PP_21);
        });

        physicalPart2_2 = dataSetInitializer.createPhysicalPart(physicalPart -> {
            physicalPart.setPart(part2);
            physicalPart.setChildRequest(childRequest2);
            physicalPart.setSerialNumber(SERIAL_NUMBER_PP_22);
        });

        childRequest2.addPhysicalPart(physicalPart2_1);
        childRequest2.addPhysicalPart(physicalPart2_2);

        filtering2_1 = dataSetInitializer.createFiltering(f -> {
            f.setPhysicalPart(physicalPart2_1);
            f.getPhysicalPart().setChildRequest(childRequest2);
        });
        filtering2_2 = dataSetInitializer.createFiltering(f -> {
            f.setPhysicalPart(physicalPart2_2);
            f.getPhysicalPart().setChildRequest(childRequest2);
        });

        drt2_1 = dataSetInitializer.createDRT(drt-> {
            drt.setFiltering(filtering2_1);
            drt.setStatus(EnumStatus.VALIDATED);
            drt.setIntegrationDate(LocalDate.now());
        });
        drt2_2 = dataSetInitializer.createDRT(drt-> {
            drt.setFiltering(filtering2_2);
            drt.setStatus(EnumStatus.VALIDATED);
            drt.setIntegrationDate(LocalDate.now().plusDays(1));
        });
        filtering2_1.setDrt(drt2_1);
        filtering2_2.setDrt(drt2_2);



        runInTransaction(() -> {
            // removing out of control DRT (we want to have only two created DRT for these tests)
            drtRepository.delete(dataset.drt_example);
            filteringRepository.save(filtering1_1);
            filteringRepository.save(filtering1_2);
            filteringRepository.save(filtering2_1);
            filteringRepository.save(filtering2_2);
            drt1_1.setChildRequest(childRequest1);
            childRequest1.addDrt(drt1_1);
            drt1_2.setChildRequest(childRequest1);
            childRequest1.addDrt(drt1_2);
            drt2_1.setChildRequest(childRequest2);
            childRequest2.addDrt(drt2_1);
            drt2_2.setChildRequest(childRequest2);
            childRequest2.addDrt(drt2_2);
            childrequestRepository.save(childRequest1);
            childrequestRepository.save(childRequest2);
            parentRequest1.addChildRequest(childRequest1);
            parentRequest2.addChildRequest(childRequest2);
            requestRepository.save(parentRequest1);
            requestRepository.save(parentRequest2);
            drtRepository.save(drt1_1);
            drtRepository.save(drt1_2);
            drtRepository.save(drt2_1);
            drtRepository.save(drt2_2);
        });
    }

    // Overall status prepared on BeforeEachMethod :
    // - There is a parentRequest1 having the ChildRequest1 which has two DRTs (drt1_1 and drt1_2). drt1_1 is associated to filtering1_1 and drt1_2 is associated to filtering_1_2
    // - There is a parentRequest2 having the ChildRequest2 which has two DRTs (drt2_1 and drt2_2). drt2_1 is associated to filtering2_1 and drt2_2 is associated to filtering_2_2
    // - operator1_1 and operator1_2 are in the operator list of parentRequest1
    // - technicalResponsible1_1 and technicalResponsible1_2 are in the technical responsible list of parentRequest1
    // - operator2_1 and operator2_2 are in the operator list of parentRequest2
    // - technicalResponsible2_1 and technicalResponsible2_2 are in the technical responsible list of parentRequest2

    // Scenario 1 : Authentified user as "Operator" but not belonging to operators of request1 or Request 2 => "expected result list size 0"
    @Test
    public void getDrtList_scenario1() {
        //operator not in group of operators of request 1 neither request 2
        User anyOperator = createUserWithRole("anyOperator", "any", RoleCode.INTERNAL_OPERATOR);
        PageDto<DrtDto> pagesDrtDto = drtService.findDrtsWithFilteringAndUserRoles(anyOperator.getId(), new DrtFilteringDto());
        assertThat(pagesDrtDto.getResults().size(),equalTo(0));

    }
    // Scenario 2 : Authentified user as "Technical responsible" but not belonging to technical responsibles of request1 or Request 2 => "expected result list size 0"
    @Test
    public void getDrtList_scenario2() {
        //technical responsible not in group of technical responsible of request 1 neither request 2
        User anyTehcnicalResponsible = createUserWithRole("anyTehcnicalResponsible", "any", RoleCode.TECHNICAL_RESPONSIBLE);
        PageDto<DrtDto> pagesDrtDto = drtService.findDrtsWithFilteringAndUserRoles(anyTehcnicalResponsible.getId(), new DrtFilteringDto());
        assertThat(pagesDrtDto.getResults().size(),equalTo(0));

    }

    // Scenario 4 : Authentified user is operator1_1 and is assigned to drt1_1 => expected result 2 "drt1_1 and drt1_2"
    @Test
    public void getDrtList_scenario4() {
        runInTransaction(()-> {
            drt1_1.setAssignedOperator(operator1_1);
            drtRepository.save(drt1_1);
        });

        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(operator1_1.getId(), new DrtFilteringDto()),
                new ArrayList<>(Arrays.asList(drt1_1, drt1_2))
        );

    }
    // Scenario 5 : Authentified user is operator1_1 and is assigned to drt1_1 and to drt1_2=> expected result 2 "drt1_1 and drt1_2"
    @Test
    public void getDrtList_scenario5() {
        runInTransaction(()-> {
            drt1_1.setAssignedOperator(operator1_1);
            drt1_2.setAssignedOperator(operator1_1);
            drtRepository.save(drt1_1);
            drtRepository.save(drt1_2);
        });
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(operator1_1.getId(), new DrtFilteringDto()),
                new ArrayList<>(Arrays.asList(drt1_1, drt1_2))
        );

    }
    // Scenario 6 : Authentified user is operator1_1 and is assigned to drt1_1 , operator1_2 is assigned to drt1_2 => expected result 1 "drt1_1" (drt1_2 is not retrieved as it is assigned to another operator)
    @Test
    public void getDrtList_scenario6() {
        runInTransaction(()-> {
            drt1_1.setAssignedOperator(operator1_1);
            drt1_2.setAssignedOperator(operator1_2);
            drtRepository.save(drt1_1);
            drtRepository.save(drt1_2);
        });
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(operator1_1.getId(), new DrtFilteringDto()),
                new ArrayList<>(Arrays.asList(drt1_1))
        );

    }
    // Scenario 7 : Authentified user is technicalResponsible1_1, with assigned operators to DRTS => expected result list is drt1_1 and drt1_2
    @Test
    public void getDrtList_scenario7() {
        runInTransaction(()-> {
            drt1_1.setAssignedOperator(operator1_1);
            drt1_2.setAssignedOperator(operator1_2);
            drtRepository.save(drt1_1);
            drtRepository.save(drt1_2);
        });
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(technicalResponsible1_1.getId(), new DrtFilteringDto()),
                new ArrayList<>(Arrays.asList(drt1_1,drt1_2))
        );

    }
    // Scenario 7_1: Authentified user is technicalResponsible1_1 => expected result list is drt1_1 and drt1_2
    @Test
    public void getDrtList_scenario7_1() {
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(technicalResponsible1_1.getId(), new DrtFilteringDto()),
                new ArrayList<>(Arrays.asList(drt1_1,drt1_2))
        );
    }

    // Scenario 8 : Authentified user is Dynamic Expert with assigned operators to DRTS  => expected result list is drt1_1 , drt1_2, drt2_1 and drt2_2
    // Scenario 9 : Authentified user is Manager with assigned operators to DRTS => expected result list is drt1_1 , drt1_2, drt2_1 and drt2_2
    @Test
    public void getDrtList_scenario8_and_9() {
        User dynamicExpert = createUserWithRole("dynamicExpert", "de", RoleCode.DYNAMIC_EXPERT);
        User manager = createUserWithRole("manager", "mgr", RoleCode.MANAGER);
        runInTransaction(()-> {
            drt1_1.setAssignedOperator(operator1_1);
            drt1_2.setAssignedOperator(operator1_2);
            drtRepository.save(drt1_1);
            drtRepository.save(drt1_2);
        });
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(dynamicExpert.getId(), new DrtFilteringDto()),
                new ArrayList<>(Arrays.asList(drt1_1,drt1_2,drt2_1,drt2_2))
        );
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(manager.getId(), new DrtFilteringDto()),
                new ArrayList<>(Arrays.asList(drt1_1,drt1_2,drt2_1,drt2_2))
        );

    }
    // Scenario 8_1 : Authentified user is Dynamic Expert  => expected result list is drt1_1 , drt1_2, drt2_1 and drt2_2
    // Scenario 9_1 : Authentified user is Manager => expected result list is drt1_1 , drt1_2, drt2_1 and drt2_2
    @Test
    public void getDrtList_scenario8_1_and_9_1() {
        User dynamicExpert = createUserWithRole("dynamicExpert", "de", RoleCode.DYNAMIC_EXPERT);
        User manager = createUserWithRole("manager", "mgr", RoleCode.MANAGER);
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(dynamicExpert.getId(), new DrtFilteringDto()),
                new ArrayList<>(Arrays.asList(drt1_1,drt1_2,drt2_1,drt2_2))
        );
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(manager.getId(), new DrtFilteringDto()),
                new ArrayList<>(Arrays.asList(drt1_1,drt1_2,drt2_1,drt2_2))
        );
    }
    // Scenario 10 : Authentified user is operator1_1 and is assigned to drt1_1, and operator1_2 is assigned to drt1_2, we make a search using the partNumber  PART_NUMBER1 , the result should be only drt1_1
    // This is combination of two rules
    @Test
    public void getDrtList_scenario10() {
        runInTransaction(()-> {
            drt1_1.setAssignedOperator(operator1_1);
            drtRepository.save(drt1_1);
            drt1_2.setAssignedOperator(operator1_2);
            drtRepository.save(drt1_2);
        });
        DrtFilteringDto filteringDto = new DrtFilteringDto();
        filteringDto.setPartNumber(PART_NUMBER1);
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(operator1_1.getId(), filteringDto),
                new ArrayList<>(Arrays.asList(drt1_1))
        );
    }
    // Scenario 10_1 : Authentified user is operator1_1 and is assigned to drt1_1, and operator1_2 is assigned to drt1_2, we make a search using the non existing partNumber , the result should be 0 drt
    // This is combination of two rules

    @Test
    public void getDrtList_scenario10_1() {
        runInTransaction(()-> {
            drt1_1.setAssignedOperator(operator1_1);
            drtRepository.save(drt1_1);
            drt1_2.setAssignedOperator(operator1_2);
            drtRepository.save(drt1_2);
        });
        DrtFilteringDto filteringDto = new DrtFilteringDto();
        filteringDto.setPartNumber("908");
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(operator1_1.getId(), filteringDto),
                new ArrayList<>(Arrays.asList())
        );
    }
    // Scenario 11 : Authentified user is operator1_1  we make a search using the partNumber  PART_NUMBER1 , the result should be only drt1_1 and drt1_2
    @Test
    public void getDrtList_scenario11() {
        DrtFilteringDto filteringDto = new DrtFilteringDto();
        filteringDto.setPartNumber(PART_NUMBER1);
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(operator1_1.getId(), filteringDto),
                new ArrayList<>(Arrays.asList(drt1_1, drt1_2))
        );
    }

    // Scenario 12 : Authentified user is operator1_1 and is assigned to drt1_1, we make a search using the partNumber  PART_NUMBER2, no result should be found
    @Test
    public void getDrtList_scenario12() {
        runInTransaction(()-> {
            drt1_1.setAssignedOperator(operator1_1);
            drtRepository.save(drt1_1);
            drt1_2.setAssignedOperator(operator1_2);
            drtRepository.save(drt1_2);
        });
        DrtFilteringDto filteringDto = new DrtFilteringDto();
        filteringDto.setPartNumber(PART_NUMBER2);
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(operator1_1.getId(), filteringDto),
                new ArrayList<>(Arrays.asList())
        );
    }
    // Scenario 13 : Authentified user is operator1_1 and is assigned to drt1_1 and operator_1_2 is assigned to drt1_2, we make a search using the serial number  SERIAL_NUMBER_PP_11, the result is drt1_1
    @Test
    public void getDrtList_scenario13() {
        runInTransaction(()-> {
            drt1_1.setAssignedOperator(operator1_1);
            drtRepository.save(drt1_1);
            drt1_2.setAssignedOperator(operator1_2);
            drtRepository.save(drt1_2);
        });
        DrtFilteringDto filteringDto = new DrtFilteringDto();
        filteringDto.setSerialNumber(SERIAL_NUMBER_PP_11);
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(operator1_1.getId(), filteringDto),
                new ArrayList<>(Arrays.asList(drt1_1))
        );
    }
    // Scenario 14 : Authentified user is operator1_1 and is not assinged, we make a search using the serial number  SERIAL_NUMBER_PP_11, 1 result drt1_1
    @Test
    public void getDrtList_scenario14() {

        DrtFilteringDto filteringDto = new DrtFilteringDto();
        filteringDto.setSerialNumber(SERIAL_NUMBER_PP_11);
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(operator1_1.getId(), filteringDto),
                new ArrayList<>(Arrays.asList(drt1_1))
        );
    }
    // Scenario 15 : Authentified user is operator1_1 and is not assinged, we make a search using the serial number  SERIAL_NUMBER_PP_12, 1 result drt1_2
    @Test
    public void getDrtList_scenario15() {

        DrtFilteringDto filteringDto = new DrtFilteringDto();
        filteringDto.setSerialNumber(SERIAL_NUMBER_PP_12);
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(operator1_1.getId(), filteringDto),
                new ArrayList<>(Arrays.asList(drt1_2))
        );
    }
    // Scenario 16 : Authentified user is operator1_1 and is not assinged, we make a search using the serial number  SERIAL_NUMBER_PP_21, no result !
    @Test
    public void getDrtList_scenario16() {

        DrtFilteringDto filteringDto = new DrtFilteringDto();
        filteringDto.setSerialNumber(SERIAL_NUMBER_PP_21);
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(operator1_1.getId(), filteringDto),
                new ArrayList<>(Arrays.asList())
        );
    }
    // Scenario 17 : Authentified user is dynamic expert we make a search using the part number PART_NUMBER1 , the result is drt1_1 and drt1_2
    @Test
    public void getDrtList_scenario17() {
        User dynamicExpert = createUserWithRole("dynamicExpert", "de", RoleCode.DYNAMIC_EXPERT);
        User manager = createUserWithRole("manager", "mgr", RoleCode.MANAGER);
        DrtFilteringDto filteringDto = new DrtFilteringDto();
        filteringDto.setPartNumber(PART_NUMBER1);
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(dynamicExpert.getId(), filteringDto),
                new ArrayList<>(Arrays.asList(drt1_1,drt1_2))
        );
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(manager.getId(), filteringDto),
                new ArrayList<>(Arrays.asList(drt1_1,drt1_2))
        );
        filteringDto.setPartNumber("11000000"); //only a prefix of the part number
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(manager.getId(), filteringDto),
                new ArrayList<>(Arrays.asList(drt1_1,drt1_2,drt2_1,drt2_2))
        );
        filteringDto.setSerialNumber("011"); // only prefix of the serial number
        checkResult(
                drtService.findDrtsWithFilteringAndUserRoles(manager.getId(), filteringDto),
                new ArrayList<>(Arrays.asList(drt1_1,drt1_2,drt2_1))
        );

    }
    private User createUserWithRole(final String userName, final String userLastName, final RoleCode roleCode) {

        User newUserWithRequiredRole = dataSetInitializer.createUser(user -> {
            user.setFirstName(userName);
            user.setLastName(userLastName);
            user.setEmail(userName + "." + userLastName + "@airbus.com");
        });
        // possibility to have a user without any role...yepa
        if (roleCode != null) {
            Role createdRole = dataSetInitializer.createRole(dataset.airbusEntity_france, roleCode);
            dataSetInitializer.createUserRole(createdRole, newUserWithRequiredRole);
        }
        return newUserWithRequiredRole;
    }

    private void checkResult(final PageDto<DrtDto> result, final List<Drt> expectedDrts) {

        List<Long> resultIds = result.getResults().stream()
                .map(drtDto -> Long.valueOf(drtDto.getId()))
                .collect(Collectors.toList());
        List<Long>  expectedIds = expectedDrts.stream()
                .map(drt-> Long.valueOf(drt.getId()))
                .collect(Collectors.toList());
        // splitted in two asserts for better bug fix
        assertTrue(result.getResults().size() == expectedDrts.size(),
                "Expecting a result size of " +expectedDrts.size() + " but result has size of " + result.getResults().size()  );
        assertTrue(resultIds.containsAll(expectedIds) , "Results are not as expected");
    }
}