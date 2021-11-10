package com.airbus.retex.filtering;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.filtering.FilteringFullDto;
import com.airbus.retex.business.dto.filtering.FilteringUpdateDto;
import com.airbus.retex.model.common.EnumFilteringPosition;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.service.filtering.IFilteringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


public class FilteringControllerPutFilteringIT extends BaseControllerTest {

    public static final String API_FILTERING_UPDATE = "/api/filterings/{id}";

    @Autowired
    IFilteringService filteringService;

    Filtering filtering;
    FilteringUpdateDto dto;

    @BeforeEach
    public void setup() {
        dto = new FilteringUpdateDto();
        filtering = dataSetInitializer.createFiltering(f -> {
            f.setPhysicalPart(dataSetInitializer.createPhysicalPart());
            f.setPosition(EnumFilteringPosition.UP);
            f.setNotification("default notification");
        });
    }

    @Test
    public void putFiltering_OK() throws Exception {
        String aircraftSerialNumber = "15615615";
        Media media = dataset.media_1;
        dto.setAircraftSerialNumber(aircraftSerialNumber);
        dto.setMedias(List.of(media.getUuid()));

        asUser = dataset.user_superAdmin;
        withRequest = put(API_FILTERING_UPDATE, filtering.getId()).content(objectMapper.writeValueAsBytes(dto));
        expectedStatus = HttpStatus.OK;
        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$.id").value(filtering.getId()));

        FilteringFullDto filteringFullDto = filteringService.findFilteringById(filtering.getId(), Language.EN);
        assertEquals(aircraftSerialNumber, filteringFullDto.getAircraftSerialNumber());
        assertEquals(media.getUuid(),filteringFullDto.getMedias().iterator().next().getUuid());
    }

    @Test
    public void putFiltering_KO_forbidden() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = put(API_FILTERING_UPDATE, filtering.getId()).content(objectMapper.writeValueAsBytes(dto));
        expectedStatus = HttpStatus.FORBIDDEN;
        ResultActions res = abstractCheck();
    }

    @Test
    public void putFiltering_KO_invalid_aircraftSerialNumber() throws Exception {
        dto.setAircraftSerialNumber("invalid");
        asUser = dataset.user_superAdmin;
        withRequest = put(API_FILTERING_UPDATE, filtering.getId()).content(objectMapper.writeValueAsBytes(dto));
        expectedStatus = HttpStatus.BAD_REQUEST;
        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$.messages").value("must match \"^[0-9]*$\""));
    }

    @Test
    public void putFiltering_KO_invalid_notification() throws Exception {
        dto.setNotification("invalid");
        asUser = dataset.user_superAdmin;
        withRequest = put(API_FILTERING_UPDATE, filtering.getId()).content(objectMapper.writeValueAsBytes(dto));
        expectedStatus = HttpStatus.BAD_REQUEST;
        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$.messages").value("must match \"^[A-Z0-9]*$\""));
    }
}
