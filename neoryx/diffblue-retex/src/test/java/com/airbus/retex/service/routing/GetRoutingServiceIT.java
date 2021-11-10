package com.airbus.retex.service.routing;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.routing.RoutingCreatedDto;
import com.airbus.retex.business.dto.routing.RoutingCreationDto;
import com.airbus.retex.business.dto.routing.RoutingDto;
import com.airbus.retex.business.dto.routing.RoutingFilteringDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.routing.RoutingFieldsEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GetRoutingServiceIT extends AbstractServiceIT {

    private static final Integer FIRST_PAGE = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 20;
    @Autowired
    private IRoutingService routingService;

    private RoutingCreationDto routingCreationDto;
    private RoutingFilteringDto routingFilteringDto;
    private Part part;

    @BeforeEach
    public void before() {
        part = dataSetInitializer.createPart(null);
        dataSetInitializer.createFunctionalArea(functionalArea -> functionalArea.setPart(part));
        routingCreationDto = new RoutingCreationDto();
        routingCreationDto.setPartId(part.getNaturalId());
        routingCreationDto.setPartNumberRoot(null);
        Map<RoutingFieldsEnum, String> mapFR = new HashMap<>();
        mapFR.put(RoutingFieldsEnum.name, "gamme 1");
        Map<RoutingFieldsEnum, String> mapEN = new HashMap<>();
        mapEN.put(RoutingFieldsEnum.name, "routing 1");
        Map<Language, Map<RoutingFieldsEnum, String>> translatedFields = new HashMap<>();
        translatedFields.put(Language.FR, mapFR);
        translatedFields.put(Language.EN, mapEN);
        routingCreationDto.setTranslatedFields(translatedFields);
        routingFilteringDto = new RoutingFilteringDto();
        routingFilteringDto.setPage(FIRST_PAGE);
        routingFilteringDto.setSize(DEFAULT_PAGE_SIZE);
        routingFilteringDto.setPartNumber(part.getPartNumber());
    }

    @Test
    public void findAllRouting_deletableTrue() throws Exception {
        routingService.createRouting(routingCreationDto, dataset.user_superAdmin);
        PageDto<RoutingDto> pageRoutingDtos = routingService.findRoutings(routingFilteringDto);
        assertThat(pageRoutingDtos.getResults().get(0).isDeletable(), is(true));
    }


    @Test
    public void findAllRouting_deletableFalse() throws Exception {
        RoutingCreatedDto routingCreatedDto = routingService.createRouting(routingCreationDto, dataset.user_superAdmin);

        dataSetInitializer.createChildRequest((cr) -> {
            cr.setRoutingNaturalId(routingCreatedDto.getRoutingIds().get(0));
        });

        routingService.setRoutingStatusToValidated(routingCreatedDto.getRoutingIds().get(0));
        PageDto<RoutingDto> pageRoutingDtos = routingService.findRoutings(routingFilteringDto);
        assertThat(pageRoutingDtos.getResults().get(0).isDeletable(), is(false));
    }
}
