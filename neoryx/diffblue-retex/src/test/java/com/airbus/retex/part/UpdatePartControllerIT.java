package com.airbus.retex.part;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.part.Part;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class UpdatePartControllerIT extends BaseControllerTest {

    private ObjectNode withBody;
    private Part part;

    @BeforeEach
    public void before() {
        part = dataSetInitializer.createPart(
                part -> {
                    part.setAta(dataset.ata_1);
                    part.setPartDesignation(dataset.partDesignation_planetGear);
                    part.setPartNumber("");
                    part.setPartNumberRoot("AH12");
                    part.setStatus(EnumStatus.IN_PROGRESS);

                }, null
        );

        dataSetInitializer.createATA(
                ata -> ata.setCode("15")
        );

        dataSetInitializer.createUserFeature(FeatureCode.PART_MAPPING, dataset.user_simpleUser, EnumRightLevel.WRITE);

        withBody = objectMapper.createObjectNode();

        ArrayNode npms = objectMapper.createArrayNode();
        npms.add("142647A72");

        withBody.put("partNumber", "332132302101");
        withBody.put("partNumberRoot", "");
        withBody.put("partDesignationId", 1);
        withBody.put("ataCode", "15");
        withBody.set("mpnCodes", npms);
    }


    private ResultActions check(Part part) throws Exception {
        return check(part.getNaturalId());
    }

    private ResultActions check(Long partId) throws Exception {
        withRequest = put("/api/parts/" + partId).content(withBody.toString());
        return abstractCheck();
    }

    @Test
    public void updatePart_ok() throws Exception {
        ArrayNode npms = objectMapper.createArrayNode();
        npms.add("142647A72");
        npms.add("142647H72");
        withBody.set("mpnCodes", npms);
        expectedStatus = HttpStatus.NO_CONTENT;
        asUser = dataset.user_simpleUser;

        check(dataset.part_example);
    }


    @Test
    public void updatePart_forbidden() throws Exception {
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser2;

        check(dataset.part_example);
    }

    @Test
    public void updatePart_WithoutMPN() throws Exception {
        withBody.put("mpnCodes", objectMapper.createArrayNode());

        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_superAdmin;
        check(dataset.part_example);
    }

    @Test
    public void updatePart_WithoutMPN_pnRoot() throws Exception {
        withBody.put("mpnCodes", objectMapper.createArrayNode());

        expectedStatus = HttpStatus.NO_CONTENT;
        asUser = dataset.user_superAdmin;
        check(part);
    }
}
