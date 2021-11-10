package com.airbus.retex.request;
import com.airbus.retex.BaseControllerTest;

import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.ata.ATA;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.request.Request;

import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.request.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;


import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class RequestControllerGetVersionsIT extends BaseControllerTest {

    @Autowired
    private RequestRepository requestRepository;

    private Request parentRequest1;

    private static final String AN_ATA_CODE = "an_ata_code";
    private static final String CREATED_REF = "ref_001_2019";
    private static final String VALIDATED_REF = "ref_002_2019";

    private ATA ata;
    private static final  String API_REQUESTS = "/api/requests";

    @BeforeEach
    public void before() {
        ata = dataSetInitializer.createATA(a-> {a.setCode(AN_ATA_CODE);});
        parentRequest1 = dataSetInitializer.createRequest("parentRequest",
            r -> {r.setVersion(1L);r.setStatus(EnumStatus.CREATED);}
        );

        // Envers 1st revision with RevisionType.ADD
        // Retex version 1.0
        runInTransaction(() -> {
            User operator1 = dataSetInitializer.createUser(user -> {
                user.setFirstName("Operator1");
                user.setLastName("1");
                user.setEmail("operator1@airbus.com");
            });
            User operator2 = dataSetInitializer.createUser(user -> {
                user.setFirstName("Operator2");
                user.setLastName("2");
                user.setEmail("operator2@airbus.com");
            });
            Role roleOperator = dataSetInitializer.createRole(dataset.airbusEntity_france, RoleCode.INTERNAL_OPERATOR);
            dataSetInitializer.createUserRole(roleOperator, operator1);
            dataSetInitializer.createUserRole(roleOperator, operator2);

            User techResponsible = dataSetInitializer.createUser(user -> {
                user.setFirstName("techResponsible1");
                user.setLastName("1");
                user.setEmail("techResponsible@airbus.com");
            });
            Role roleTechResponsible = dataSetInitializer.createRole(dataset.airbusEntity_france, RoleCode.TECHNICAL_RESPONSIBLE);
            dataSetInitializer.createUserRole(roleTechResponsible, operator2);

            parentRequest1 = requestRepository.getOne(parentRequest1.getId());

            parentRequest1.setAta(ata);
            parentRequest1.addOperator(operator1);
            parentRequest1.addOperator(operator2);
            parentRequest1.addTechnicalResponsible(techResponsible);

            parentRequest1 = requestRepository.save(parentRequest1);
            // Envers 2st revision with RevisionType.MOD
            // Retex version 1.0
        });
        runInTransaction(() -> {
            parentRequest1.setAta(dataset.ata_2);
            parentRequest1.setReference(CREATED_REF);
            parentRequest1 = requestRepository.save(parentRequest1);
        });
        runInTransaction(() -> {
            // Envers 3st revision with RevisionType.MOD
            // Retex version 1.0
            parentRequest1.setAircraftFamily(dataset.aircraft_family_2);
            parentRequest1 = requestRepository.save(parentRequest1);
        });
        // Envers 4st revision with RevisionType.MOD
        // Retex version 1.0
        runInTransaction(() -> {
            parentRequest1.setStatus(EnumStatus.VALIDATED);
            parentRequest1.setVersion(parentRequest1.getVersion() + 1);
            parentRequest1.setReference(VALIDATED_REF);
            parentRequest1 = requestRepository.save(parentRequest1);
            // Envers 5st revision with RevisionType.MOD
            // Retex version 2.0
        });
        runInTransaction(() -> {
            parentRequest1.setAircraftFamily(dataset.aircraft_family_1);
            parentRequest1.setVersion(parentRequest1.getVersion() + 1);
            parentRequest1 = requestRepository.save(parentRequest1);
            // Envers 6st revision with RevisionType.MOD
            // Retex version 3.0
        });
    }

    @Test
    public void getRequestSpecificVersion() throws Exception {
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST,asUser , EnumRightLevel.READ);
        expectedStatus = HttpStatus.OK;

        ResultActions result = check(API_REQUESTS + "/" + parentRequest1.getId() +"?version=2");

        result
            .andExpect(jsonPath("$.id").value(parentRequest1.getId()))
            .andExpect(jsonPath("$.reference", equalTo(VALIDATED_REF)))
            .andExpect(jsonPath("$.versionNumber", equalTo(2)))
            .andExpect(jsonPath("$.status", equalTo(EnumStatus.VALIDATED.name())))
            .andExpect(jsonPath("$.creationDate", notNullValue()))
            .andExpect(jsonPath("$.dueDate", notNullValue()))
            .andExpect(jsonPath("$.requester.id").value(dataset.user_superAdmin.getId()))
            .andExpect(jsonPath("$.requester.firstName").value(dataset.user_superAdmin.getFirstName()))
            .andExpect(jsonPath("$.requester.lastName").value(dataset.user_superAdmin.getLastName()))
            .andExpect(jsonPath("$.origin.id").value(dataset.ORIGIN_CIVP.getId()))
            .andExpect(jsonPath("$.origin.name").value(dataset.ORIGIN_CIVP.getName()))
            .andExpect(jsonPath("$.origin.color").value(dataset.ORIGIN_CIVP.getColor()))
            .andExpect(jsonPath("$.airbusEntity.id").value(dataset.airbusEntity_france.getId()))
            .andExpect(jsonPath("$.airbusEntity.code").value(dataset.airbusEntity_france.getCode()))
            .andExpect(jsonPath("$.airbusEntity.countryName").value(dataset.airbusEntity_france.getCountryName()))
            .andExpect(jsonPath("$.operators", hasSize(3)))
            .andExpect(jsonPath("$.operators[*].firstName", hasItems("jean louis", "Operator1", "Operator2")))
            .andExpect(jsonPath("$.technicalResponsibles", hasSize(2)))
            .andExpect(jsonPath("$.technicalResponsibles[*].firstName", hasItems("jean louis", "techResponsible1")))
            .andExpect(jsonPath("$.isDeletable").value(true))
            .andExpect(jsonPath("$.name").value("parentRequest" + parentRequest1.getId() + " (EN)"))
            .andExpect(jsonPath("$.description").value("Description in english for parentRequest" + parentRequest1.getId() + " (EN)"));
    }

    @Test
    public void getRequestVersions_ID_Not_Exisiting_empty_list_returned() throws Exception {
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST,asUser , EnumRightLevel.READ);
        expectedStatus = HttpStatus.OK;
        // A request with ID 1234 is not existing, empty result must be returned
        ResultActions result = check(API_REQUESTS + "/" + 1234L +"/versions");

        result.andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getRequestVersions_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST,asUser , EnumRightLevel.READ);
        expectedStatus = HttpStatus.OK;
        // We created parenteRequest1 (STATUS CREATED) and after several modifications (still STATUS CREATED)
        // we have validated it (V2)....then we have done some modifications on it (V3)
        // The result of getVersions will collapse the CREATED one to only one latest , so we should have only three
        // Versions as below:
        //[
        // {"dateUpdate":"2019-10-23T17:38:07.758","versionNumber":1,"status":"CREATED","isLatestVersion":false},
        // {"dateUpdate":"2019-10-23T17:38:07.778","versionNumber":2,"status":"VALIDATED","isLatestVersion":false},
        // {"dateUpdate":"2019-10-23T17:38:07.796","versionNumber":3,"status":"VALIDATED","isLatestVersion":true}
        // ]

        ResultActions result = check(API_REQUESTS + "/" + parentRequest1.getId() +"/versions");
        result.andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$.[0].isLatestVersion",is(false)))
            .andExpect(jsonPath("$.[0].versionNumber",is(1)))
            .andExpect(jsonPath("$.[0].status",is(EnumStatus.CREATED.toString())))
            .andExpect(jsonPath("$.[1].isLatestVersion",is(false)))
            .andExpect(jsonPath("$.[1].versionNumber",is(2)))
            .andExpect(jsonPath("$.[1].status",is(EnumStatus.OBSOLETED.toString())))
            .andExpect(jsonPath("$.[2].isLatestVersion",is(true)))
            .andExpect(jsonPath("$.[2].versionNumber",is(3)))
            .andExpect(jsonPath("$.[2].status", is(EnumStatus.VALIDATED.toString())));
    }

    private ResultActions check(String endpoint) throws Exception {
        withRequest = get(endpoint);

        return abstractCheck();
    }

}
