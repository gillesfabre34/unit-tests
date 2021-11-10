package com.airbus.retex.service.routing;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.routing.RoutingCreatedDto;
import com.airbus.retex.business.dto.routing.RoutingCreationDto;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routing.RoutingFieldsEnum;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.persistence.operation.OperationFunctionalAreaRepository;
import com.airbus.retex.persistence.operation.OperationRepository;
import com.airbus.retex.persistence.routing.RoutingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CreateRoutingServiceIT extends AbstractServiceIT {

    @Autowired
    private IRoutingService routingService;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private RoutingRepository routingRepository;

    @Autowired
    private OperationFunctionalAreaRepository operationFunctionalAreaRepository;

    private RoutingCreationDto routingCreationDto;

    @BeforeEach
    public void before() {

        RoutingComponent routingComponent = dataSetInitializer.createRoutingComponent(rc -> {
            rc.setFunctionality(dataset.functionality_teeth);
        });
        dataSetInitializer.createRoutingComponentIndex(routingComponent.getTechnicalId(), null);

        routingCreationDto = new RoutingCreationDto();
        routingCreationDto.setPartId(dataset.part_example.getNaturalId());
        routingCreationDto.setPartNumberRoot(null);
        Map<RoutingFieldsEnum, String> mapFR = new HashMap<>();
        mapFR.put(RoutingFieldsEnum.name, "gamme 1");
        Map<RoutingFieldsEnum, String> mapEN = new HashMap<>();
        mapEN.put(RoutingFieldsEnum.name, "routing 1");
        Map<Language, Map<RoutingFieldsEnum, String>> translatedFields = new HashMap<>();
        translatedFields.put(Language.FR, mapFR);
        translatedFields.put(Language.EN, mapEN);
        routingCreationDto.setTranslatedFields(translatedFields);
    }

    @Test
    public void createRouting_ok() throws Exception {
        RoutingCreatedDto result = routingService.createRouting(routingCreationDto, dataset.user_superAdmin);
        Routing createdRouting = routingRepository.findLastVersionByNaturalId(result.getRoutingIds().get(0)).orElse(null);

        assertThat(result.getRoutingIds().size() > 0, is(true));
        assertThat(result.getErrorRoutingExists() == null, is(true));
        assertThat(operationRepository.findOperationByRoutingTechnicalId(createdRouting.getTechnicalId()),
                hasSize(greaterThan(0)));
        assertThat(operationFunctionalAreaRepository.count() > 0, is(true));
    }

    @Test
    public void createRouting_EnumStatusCreated_ok() throws Exception {
        routingService.createRouting(routingCreationDto, dataset.user_superAdmin);
        Routing routing = routingRepository.findByPartTechnicalIdAndIsLatestVersionTrue(dataset.part_example.getTechnicalId()).get();
        assertThat(routing.getStatus(), equalTo(EnumStatus.CREATED));
    }


    @Test
    public void validateRouting_ok() throws Exception {
        // create routing
        routingService.createRouting(routingCreationDto, dataset.user_superAdmin);
        Routing routing = routingRepository.findByPartTechnicalIdAndIsLatestVersionTrue(dataset.part_example.getTechnicalId()).get();
        assertThat(routing.getStatus(), equalTo(EnumStatus.CREATED));

        // then validate the created routing
        routingService.setRoutingStatusToValidated(routing.getNaturalId());
        List<Routing> routings = routingRepository.findAllVersionsByNaturalId(routing.getNaturalId());
        assertThat(routings.size(), equalTo(1));
        assertThat(routings.get(0).getVersionNumber(), equalTo(1L));
        assertThat(routings.get(0).getStatus(), equalTo(EnumStatus.VALIDATED));
        assertThat(routings.get(0).getTechnicalId(), equalTo(routing.getTechnicalId()));
        assertThat(routings.get(0).getNaturalId(), equalTo(routing.getNaturalId()));
    }
}
