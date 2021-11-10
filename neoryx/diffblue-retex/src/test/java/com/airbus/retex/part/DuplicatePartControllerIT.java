package com.airbus.retex.part;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.part.Mpn;
import com.airbus.retex.model.part.Part;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class DuplicatePartControllerIT extends BaseControllerTest {

    private ObjectNode withBody;
    private Part partOne;

    @BeforeEach
    public void before() {
        dataset.partDesignation_lowerLeg = dataSetInitializer.createPartDesignation(null,null);

        Mpn mpn = dataSetInitializer.createMpn(mpn1 -> mpn1.setCode("MPNFDFSQ"));
        Set setMpn = new HashSet<>();
        setMpn.add(mpn);
        partOne = dataSetInitializer.createPart(
                part -> {
                    part.setAta(dataset.ata_1);
                    part.setPartDesignation(dataset.partDesignation_lowerLeg);
                    part.setPartNumber("AH1234567890");
                    part.setPartNumberRoot("AH12");
                    part.setStatus(EnumStatus.VALIDATED);
                }, setMpn
        );

        dataSetInitializer.createATA(
                ata -> ata.setCode("15")
        );
        dataSetInitializer.createATA(
                ata -> ata.setCode("17")
        );

        dataSetInitializer.createUserFeature(FeatureCode.PART_MAPPING, dataset.user_simpleUser, EnumRightLevel.WRITE);
        withBody = objectMapper.createObjectNode();

        ArrayNode mpns = objectMapper.createArrayNode();
        mpns.add("142647A84");
        withBody.set("mpnCodes", mpns);
        withBody.put("partNumberRoot", "");
        withBody.put("partDesignationId", dataset.partDesignation_lowerLeg.getId());
        withBody.put("ataCode", dataset.ata_1.getCode());
    }


    private ResultActions check(Part part) throws Exception {
        return check(part.getNaturalId());
    }

    private ResultActions check(Long partId) throws Exception {
        withRequest = post("/api/parts/duplicate/" + partId).content(withBody.toString());
        return abstractCheck();
    }

    @Test
    public void duplicatePart_ok() throws Exception {

        withBody.put("partNumber", "33213239");
        withBody.put("partNumberRoot", "AH12");

        ArrayNode mpns = objectMapper.createArrayNode();
        mpns.add("142647A83");
        withBody.put("partNumberRoot", "AH12");
        withBody.set("mpnCodes", mpns);

        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;

        check(partOne)
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.id", not(partOne.getNaturalId())))
                .andExpect(jsonPath("$.partNumber").value("33213239"))
                .andExpect(jsonPath("$.partNumberRoot").value(partOne.getPartNumberRoot()))
                .andExpect(jsonPath("$.ata.code").value(partOne.getAta().getCode()))
                .andExpect(jsonPath("$.partDesignation.id").value(partOne.getPartDesignation().getId()))
                .andExpect(jsonPath("$.mpnCodes").isArray())
                .andExpect(jsonPath("$.mpnCodes", hasSize(1)))
                .andExpect(jsonPath("$.mpnCodes[0].code").value("142647A83"));
    }

    @Test
    public void duplicatePart_ok_WithSetParams() throws Exception {

        withBody.put("partNumber", "332132309999");
        withBody.put("partNumberRoot", "7884145");
        withBody.put("partDesignationId", dataset.partDesignation_lowerLeg.getId());
        withBody.put("ataCode", "17");

        ArrayNode mpns = objectMapper.createArrayNode();
        mpns.add("142647A84");
        withBody.set("mpnCodes", mpns);

        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;

        check(partOne)
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.id", not(partOne.getNaturalId())))
                .andExpect(jsonPath("$.partNumber").value("332132309999"))
                .andExpect(jsonPath("$.partNumberRoot").value("7884145"))
                .andExpect(jsonPath("$.ata.code").value("17"))
                .andExpect(jsonPath("$.partDesignation.id").value(dataset.partDesignation_lowerLeg.getId()))
                .andExpect(jsonPath("$.mpnCodes").isArray())
                .andExpect(jsonPath("$.mpnCodes", hasSize(1)))
                .andExpect(jsonPath("$.mpnCodes[0].code").value("142647A84"));
    }


    @Test
    public void duplicatePart_forbidden() throws Exception {
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser2;

        check(dataset.part_example);
    }


    @Test
    public void duplicatePart_WithoutPartNumber() throws Exception {
        withBody.put("partNumber", "");

        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        check(dataset.part_example);
    }


    @Test
    public void duplicatePart_WithPartNumberMalformed() throws Exception {
        withBody.put("partNumber", "--");

        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;

        check(dataset.part_example);
    }

    @Test
    public void duplicatePart_WithoutMPN() throws Exception {
        withBody.put("mpnCodes", objectMapper.createArrayNode());
        withBody.put("partNumber", "332132309999");

        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_superAdmin;
        check(dataset.part_example);
    }

    @Test
    public void duplicatePart_WithoutMPN_PNRoot() throws Exception {
        withBody.put("partNumberRoot", "7884145");
        withBody.put("partDesignationId", dataset.partDesignation_lowerLeg.getId());
        withBody.put("ataCode", "17");
        withBody.set("mpnCodes", objectMapper.createArrayNode());

        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_superAdmin;
        check(partOne)
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.id", not(partOne.getNaturalId())))
                .andExpect(jsonPath("$.partNumber").value(nullValue()))
                .andExpect(jsonPath("$.partNumberRoot").value("7884145"))
                .andExpect(jsonPath("$.ata.code").value("17"))
                .andExpect(jsonPath("$.partDesignation.id").value(dataset.partDesignation_lowerLeg.getId()))
                .andExpect(jsonPath("$.mpnCodes").isEmpty());
    }
}
