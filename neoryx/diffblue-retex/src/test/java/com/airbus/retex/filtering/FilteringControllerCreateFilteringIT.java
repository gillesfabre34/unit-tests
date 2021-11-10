package com.airbus.retex.filtering;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.filtering.FilteringCreationDto;
import com.airbus.retex.utils.ConstantUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Locale;
import java.util.function.Consumer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class FilteringControllerCreateFilteringIT extends BaseControllerTest {

    @BeforeEach
    public void setup() {
    }

    @Test
    public void createFiltering_OK() throws Exception {
        FilteringCreationDto dto = new FilteringCreationDto();
        dto.setPartNumber(dataset.part_example_2.getPartNumber());
        dto.setSerialNumber("SN1");
        dto.setEquipmentNumber("EN1");

        asUser = dataset.user_superAdmin;
        withRequest = post(ConstantUrl.API_FILTERINGS).content(objectMapper.writeValueAsBytes(dto));
        expectedStatus = HttpStatus.OK;
        ResultActions res = abstractCheck();
    }

    @Test
    public void createFiltering_KO_forbidden() throws Exception {
        FilteringCreationDto dto = new FilteringCreationDto();
        dto.setPartNumber(dataset.part_example_2.getPartNumber());
        dto.setSerialNumber("SN1");
        dto.setEquipmentNumber("EN1");

        asUser = dataset.user_simpleUser;
        withRequest = post(ConstantUrl.API_FILTERINGS).content(objectMapper.writeValueAsBytes(dto));
        expectedStatus = HttpStatus.FORBIDDEN;
        ResultActions res = abstractCheck();
    }

    @Test
    public void createFiltering_KO_equipment_exists() throws Exception {

        String pn = dataset.filtering_example.getPhysicalPart().getPart().getPartNumber();
        String sn = dataset.filtering_example.getPhysicalPart().getSerialNumber();
        String en = dataset.filtering_example.getPhysicalPart().getEquipmentNumber();

        FilteringCreationDto dto = new FilteringCreationDto();
        dto.setPartNumber(pn);
        dto.setSerialNumber("DOESNOTEXIST");
        dto.setEquipmentNumber(en);

        asUser = dataset.user_superAdmin;
        withRequest = post(ConstantUrl.API_FILTERINGS).content(objectMapper.writeValueAsBytes(dto));
        expectedStatus = HttpStatus.BAD_REQUEST;

        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$.messages").value("This equipment number already exists for this "+pn+"/"+sn+" couple"));
    }

    @Test
    public void createFiltering_KO_equipment_exists_fr() throws Exception {
        // Checks that the quote does not prevent the expansion of parameters (the quote must be doubled in the message for this to work)

        String pn = dataset.filtering_example.getPhysicalPart().getPart().getPartNumber();
        String sn = dataset.filtering_example.getPhysicalPart().getSerialNumber();
        String en = dataset.filtering_example.getPhysicalPart().getEquipmentNumber();

        FilteringCreationDto dto = new FilteringCreationDto();
        dto.setPartNumber(pn);
        dto.setSerialNumber("DOESNOTEXIST");
        dto.setEquipmentNumber(en);

        withLocale = Locale.FRENCH;
        asUser = dataset.user_superAdmin;
        withRequest = post(ConstantUrl.API_FILTERINGS).content(objectMapper.writeValueAsBytes(dto));
        expectedStatus = HttpStatus.BAD_REQUEST;

        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$.messages").value("Ce numéro d'équipement existe déjà pour le couple "+pn+"/"+sn));
    }

    // Other functional exceptions are tested in FilteringCreateServiceIT.java

    @Test
    public void createFiltering_KO_bad_input_pn() throws Exception {
        createFiltering_test_bad_input(dto -> {dto.setPartNumber("bad");});
    }

    @Test
    public void createFiltering_KO_bad_input_sn() throws Exception {
        createFiltering_test_bad_input(dto -> {dto.setSerialNumber("bad");});
    }

    @Test
    public void createFiltering_KO_bad_input_en() throws Exception {
        createFiltering_test_bad_input(dto -> {dto.setEquipmentNumber("bad");});
    }

    private void createFiltering_test_bad_input(Consumer<FilteringCreationDto> modifyDto) throws Exception {
        FilteringCreationDto dto = new FilteringCreationDto();
        dto.setPartNumber("OK");
        dto.setSerialNumber("OK");
        dto.setEquipmentNumber("OK");
        modifyDto.accept(dto);

        asUser = dataset.user_superAdmin;
        withRequest = post(ConstantUrl.API_FILTERINGS).content(objectMapper.writeValueAsBytes(dto));
        expectedStatus = HttpStatus.BAD_REQUEST;

        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$.messages").value("must match \"^[A-Z0-9]+$\""));
    }
}
