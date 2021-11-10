package com.airbus.retex.part;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.ata.ATA;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.part.Mpn;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.persistence.part.PartRepository;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.airbus.retex.utils.ConstantUrl.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class PartControllerIT extends BaseControllerTest {

    @Autowired
    private PartRepository partRepository;
    private Part partOne;
    private Part partTwo;
    private Part partTree;
    private Part partFour;
    private ATA otherATA;
    private Mpn mpnOne;

    private final static String API_DUPLICATE_PART = "/duplicate/";
    private final static String BASE_API_PARTS = "/api/parts";
    private final static String ADD_PART_MAPPING_IMAGE = "/{id}/image";


    @BeforeEach
    public void before() {

        mpnOne = dataSetInitializer.createMpn(mpn -> mpn.setCode("MPNFQSDF"));
        HashSet mpnSet = new HashSet<>();
        mpnSet.add(mpnOne);
        partOne = dataSetInitializer.createPart(
                part -> {
                    part.setAta(dataset.ata_1);
                    part.setPartDesignation(dataset.partDesignation_planetGear);
                    part.setPartNumber("AH1234567890");
                    part.setPartNumberRoot("AH12");
                    part.setStatus(EnumStatus.CREATED);

                }, mpnSet
        );

        partTwo = dataSetInitializer.createPart(
                part -> {
                    part.setAta(dataset.ata_1);
                    part.setPartDesignation(dataset.partDesignation_lowerLeg);
                    part.setPartNumber("AH1234567891");
                    part.setPartNumberRoot("AH12");
                    part.setStatus(EnumStatus.CREATED);
                }, null
        );

        partTree = dataSetInitializer.createPart(
                part -> {
                    part.setAta(dataset.ata_2);
                    part.setPartDesignation(dataset.partDesignation_planetGear);
                    part.setPartNumber("AH1334567890");
                    part.setPartNumberRoot("AH13");
                    part.setStatus(EnumStatus.CREATED);
                }, null
        );

        partFour = dataSetInitializer.createPart(

                part -> {
                    part.setAta(dataset.ata_2);
                    part.setPartDesignation(dataset.partDesignation_lowerLeg);
                    part.setPartNumber("AH1334567891");
                    part.setPartNumberRoot("AH13");
                    part.setStatus(EnumStatus.CREATED);
                }, null
        );

        dataSetInitializer.createATA(
                ata -> ata.setCode(dataset.ATA_CODE_64)
        );

        otherATA = dataSetInitializer.createATA();

        dataSetInitializer.createUserFeature(FeatureCode.PART_MAPPING, dataset.user_simpleUser, EnumRightLevel.WRITE);
    }

    @Test
    public void getAllParts_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.OK;

        checkGetAllParts(hasSize((int) partRepository.count()));
    }

    @Test
    public void getAllParts_Paginated_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withParams.add("page", "1");
        withParams.add("size", "2");
        expectedStatus = HttpStatus.OK;

        checkGetAllParts(hasSize(2));
    }

    @Test
    public void getAllParts_PartNumberFiltered_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withParams.add("partNumber", "AH12");
        expectedStatus = HttpStatus.OK;

        checkGetAllParts(List.of(partOne, partTwo), List.of(partTree, partFour));
    }

    //@Test
    public void getAllParts_AtaFiltered_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withParams.add("ataCode", dataset.ata_1.getCode());
        withParams.add("size", "100");
        withParams.add("page", "0");
        expectedStatus = HttpStatus.OK;

        checkGetAllParts(List.of(partOne, partTwo), List.of(partTree, partFour));
    }

    @Test
    public void getAllParts_DesignationFiltered_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withParams.add("designationId", dataset.partDesignation_planetGear.getId().toString());
        expectedStatus = HttpStatus.OK;

        checkGetAllParts(List.of(partOne), List.of(partTwo));
    }

    @Test
    public void getAllParts_MultiFiltered_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        withParams.add("designationId", dataset.partDesignation_planetGear.getId().toString());
        withParams.add("ata.code", otherATA.getCode());
        withParams.add("partNumber", "AH12");
        expectedStatus = HttpStatus.OK;

        checkGetAllParts(List.of(), List.of(partOne, partTwo, partTree, partFour));
    }

    private void checkGetAllParts(List<Part> contains, List<Part> notContains) throws Exception {
        withRequest = get(BASE_API_PARTS);

        Integer[] containsIds = contains.stream().map(part -> Integer.valueOf(part.getNaturalId().intValue())).collect(Collectors.toList()).toArray(new Integer[0]);
        Integer[] notContainsId = notContains.stream().map(part -> Integer.valueOf(part.getNaturalId().intValue())).collect(Collectors.toList()).toArray(new Integer[0]);

        abstractCheck()
                .andExpect(jsonPath("$.results[*].id", hasItems(containsIds)))
                .andExpect(jsonPath("$.results[*].id", not(hasItems(notContainsId))));
    }

    private void checkGetAllParts(Matcher<? extends Collection> resultsMatcher) throws Exception {
        withRequest = get(BASE_API_PARTS);

        abstractCheck()
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results", resultsMatcher));
    }


    @Test
    public void createPart_ok() throws Exception {
        ObjectNode body = buildCreatePartBody();

        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;
        withRequest = post(API_PARTS).content(body.toString());

        abstractCheck();
    }

    @Test
    public void createPart_WithoutMPN() throws Exception {
        ObjectNode body = buildCreatePartBody();
        body.put("mpnCodes", objectMapper.createArrayNode());

        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        withRequest = post(API_PARTS).content(body.toString());

        abstractCheck().andExpect(jsonPath("$.messages", notNullValue()));
    }

    private ObjectNode buildCreatePartBody() {
        ObjectNode nodes = objectMapper.createObjectNode();

        ArrayNode npms = objectMapper.createArrayNode();
        npms.add("142647A72");

        nodes.put("partNumber", "332132302101");
        nodes.put("partNumberRoot", "");
        nodes.put("partDesignationId", dataset.partDesignation_planetGear.getId());
        nodes.put("ataCode", otherATA.getCode());
        nodes.set("mpnCodes", npms);

        return nodes;
    }

    @Test
    public void getPart_ok() throws Exception {
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;
        withRequest = get(BASE_API_PARTS + "/" + partOne.getNaturalId());

        abstractCheck()
                .andExpect(jsonPath("$.id").value(partOne.getNaturalId()))
                .andExpect(jsonPath("$.partNumber").value(partOne.getPartNumber()))
                .andExpect(jsonPath("$.partNumberRoot").value(partOne.getPartNumberRoot()))
                .andExpect(jsonPath("$.ata.code").value(partOne.getAta().getCode()))
                .andExpect(jsonPath("$.partDesignation.id").value(partOne.getPartDesignation().getId()))
                .andExpect(jsonPath("$.status").value(partOne.getStatus().toString()))
                .andExpect(jsonPath("$.mpnCodes").isArray())
                .andExpect(jsonPath("$.mpnCodes", hasSize(1)))
                .andExpect(jsonPath("$.mpnCodes[0].code").value(mpnOne.getCode()));
    }


    @Test
    public void getPart_forbidden() throws Exception {
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser2;
        withRequest = get(BASE_API_PARTS + "/" + partOne.getNaturalId());

        abstractCheck();
    }

    @Test
    public void getPart_WithBadPartId() throws Exception {
        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_simpleUser;
        withRequest = get(BASE_API_PARTS + "/0");

        abstractCheck();
    }


    @Test
    public void getDuplicatePart_ok() throws Exception {
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;
        withRequest = get(BASE_API_PARTS + API_DUPLICATE_PART + partOne.getNaturalId());

        abstractCheck()
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.partNumberRoot").value(partOne.getPartNumberRoot()))
                .andExpect(jsonPath("$.ata.code").value(partOne.getAta().getCode()))
                .andExpect(jsonPath("$.partDesignation.id").value(partOne.getPartDesignation().getId()));
    }


    @Test
    public void getDuplicatePart_forbidden() throws Exception {
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser2;
        withRequest = get(BASE_API_PARTS + API_DUPLICATE_PART + partOne.getNaturalId());

        abstractCheck();
    }

    @Test
    public void getDuplicatePart_WithBadPartId() throws Exception {
        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_simpleUser;
        withRequest = get(BASE_API_PARTS + API_DUPLICATE_PART + "/0");

        withRequest = get(BASE_API_PARTS + "/0");

        abstractCheck();
    }

    @Test
    public void deletePart_ok() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = delete(BASE_API_PARTS + API_DELETE_PART, partOne.getNaturalId());
        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }

    @Test
    public void deletePart_ko_linked_to_routing() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = delete(BASE_API_PARTS + API_DELETE_PART, dataset.part_link_routing.getNaturalId());
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck().andExpect(jsonPath("$.messages", notNullValue()));
    }

    @Test
    public void deletePartWithoutDelete_Forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        withRequest = delete(BASE_API_PARTS + API_DELETE_PART, partOne.getNaturalId());
        expectedStatus = HttpStatus.FORBIDDEN;
        // le type post_d'exception attendue et qui devrait soulevée au moment de l'appel à abstractCheck();
        abstractCheck();

    }

    @Test
    public void deletePartWithoutWrite_Forbidden() throws Exception {
        asUser = dataset.user_partReader;
        withRequest = delete(BASE_API_PARTS + API_DELETE_PART, partOne.getNaturalId());
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();

    }

    @Test
    public void deletePart_IncorrectId() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = delete(BASE_API_PARTS + API_DELETE_PART, 50);
        expectedStatus = HttpStatus.NOT_FOUND;

        abstractCheck();
    }

    @Test
    public void addPartImage_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        dataSetInitializer.createUserFeature(FeatureCode.PART_MAPPING, dataset.user_superAdmin, EnumRightLevel.WRITE);

        withRequest = multipart(BASE_API_PARTS + ADD_PART_MAPPING_IMAGE, partOne.getNaturalId())
                .file(testMultipartImage());
        expectedStatus = HttpStatus.OK;
        abstractCheck();
    }

    @Test
    public void addPartImage_IncorrectPartId() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = multipart(BASE_API_PARTS + ADD_PART_MAPPING_IMAGE, 50L)
                .file(testMultipartImage());
        dataSetInitializer.createUserFeature(FeatureCode.PART_MAPPING, dataset.user_superAdmin, EnumRightLevel.WRITE);
        expectedStatus = HttpStatus.NOT_FOUND;
        abstractCheck().andExpect(jsonPath("$.messages", notNullValue()));
    }

    @Test
    public void addPartImage_Not_Accepted_ExtensionFormat() throws Exception {
        asUser = dataset.user_superAdmin;
        MockMultipartFile file = new MockMultipartFile("file", "file.bpm", "text/plain", "some xml".getBytes());
        withRequest = multipart(BASE_API_PARTS + ADD_PART_MAPPING_IMAGE, partOne.getNaturalId()).file(file);
        expectedStatus = HttpStatus.BAD_REQUEST;
        checkFunctionalException("retex.error.media.format.denied");
    }

    @Test
    public void addPartImage_Accepted_ExtensionFormat() throws Exception {
        asUser = dataset.user_superAdmin;
        MockMultipartFile fileFormatPNG = new MockMultipartFile("file", "file.png", "text/plain", "some xml".getBytes());
        MockMultipartFile fileFormatJPEG = new MockMultipartFile("file", "file.jpeg", "text/plain", "some xml".getBytes());
        withRequest = multipart(BASE_API_PARTS + ADD_PART_MAPPING_IMAGE, partOne.getNaturalId()).file(fileFormatPNG).file(fileFormatJPEG);
        expectedStatus = HttpStatus.OK;
        abstractCheck().andExpect(jsonPath("$.filename", notNullValue()))
                .andExpect(jsonPath("$.uuid", notNullValue()));
    }

}
