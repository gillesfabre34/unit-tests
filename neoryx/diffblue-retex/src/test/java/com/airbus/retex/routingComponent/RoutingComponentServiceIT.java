package com.airbus.retex.routingComponent;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.step.StepCreationDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.mapper.CloningContext;
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.persistence.routingComponent.RoutingComponentIndexRepository;
import com.airbus.retex.service.impl.routingComponent.mapper.RoutingComponentIndexCloner;
import com.airbus.retex.service.routingComponent.IRoutingComponentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@Transactional
public class RoutingComponentServiceIT extends AbstractServiceIT {

    @Autowired
    RoutingComponentIndexRepository routingComponentIndexRepository;
    @Autowired
    DtoConverter dtoConverter;
    @Autowired
    IRoutingComponentService routingComponentService;

    private RoutingComponentIndex routingComponentIndex;
    private RoutingComponentIndex routingComponentIndexValidated;
    private Damage damage;

    @Autowired
    private RoutingComponentIndexCloner routingComponentIndexCloner;

    @BeforeEach
    public void before() {
        RoutingComponent routingComponent = dataSetInitializer.createRoutingComponent();
        routingComponentIndex = dataSetInitializer.createRoutingComponentIndex(routingComponent.getTechnicalId(), null, routingComponentIndex1 -> {
            routingComponentIndex1.setStatus(EnumStatus.CREATED);
            routingComponentIndex1.setCreationDate(LocalDateTime.now());
        });

        damage = dataSetInitializer.createDamage(EnumActiveState.ACTIVE, "damage");

        RoutingComponent routingComponent2 = dataSetInitializer.createRoutingComponent(routingComponent1 -> {
            routingComponent1.setOperationType(dataset.operationType_laboratory);
            routingComponent1.setDamage(damage);
        });

        routingComponentIndexValidated = dataSetInitializer.createRoutingComponentIndex(routingComponent2.getTechnicalId(), null, routingComponentIndex1 -> {
            routingComponentIndex1.setStatus(EnumStatus.VALIDATED);
            routingComponentIndex1.setCreationDate(LocalDateTime.now());

        });
    }

    @Test
    public void saveNewRoutingComponentVersion() throws FunctionalException {

        RoutingComponentIndex newRoutingComponentIndex = routingComponentIndexCloner.cloneRoutingComponentIndex(
                routingComponentIndexValidated, new CloningContext()
        );

        RoutingComponentCreateUpdateDto routingComponentCreateUpdateDto = new RoutingComponentCreateUpdateDto();
        routingComponentCreateUpdateDto.setOperationTypeId(newRoutingComponentIndex.getRoutingComponent().getOperationType().getId());
        routingComponentCreateUpdateDto.setInspectionValue(newRoutingComponentIndex.getRoutingComponent().getInspection().getValue());
        routingComponentCreateUpdateDto.setSubTaskId(newRoutingComponentIndex.getRoutingComponent().getDamageId());
        routingComponentCreateUpdateDto.setTaskId(newRoutingComponentIndex.getRoutingComponent().getFunctionality().getId());
        routingComponentCreateUpdateDto.setSteps(dtoConverter.toDtos(newRoutingComponentIndex.getRoutingComponent().getSteps(), StepCreationDto.class));

        routingComponentService.updateRoutingComponent(routingComponentCreateUpdateDto, newRoutingComponentIndex.getNaturalId(), true);

        Optional<RoutingComponentIndex> routingComponentIndexToTest = routingComponentIndexRepository.findLastVersionByNaturalId(newRoutingComponentIndex.getNaturalId());
        assertThat(routingComponentIndexToTest.isPresent(), equalTo(true));
        assertThat(routingComponentIndexToTest.get().getStatus(), equalTo(EnumStatus.VALIDATED));
        assertThat(routingComponentIndexToTest.get().getVersionNumber(), equalTo(routingComponentIndexValidated.getVersionNumber() +1));
        assertThat(routingComponentIndexToTest.get().getTechnicalId(), not(equalTo(routingComponentIndexValidated.getTechnicalId())));
        assertThat(routingComponentIndexToTest.get().getNaturalId(), equalTo(routingComponentIndexValidated.getNaturalId()));
    }

    @Test
    public void findAllRoutingComponentVersionMustFindOnlyLatestVersion(){
        List<RoutingComponentIndex> routingComponentListBeforeNewVersion =  routingComponentIndexRepository.findAllLastVersions();

        List<RoutingComponentIndex> routingComponentIndexListList =  routingComponentIndexRepository.findAllLastVersions();
        assertThat(routingComponentIndexListList.size(), equalTo(routingComponentListBeforeNewVersion.size()));
    }

}
