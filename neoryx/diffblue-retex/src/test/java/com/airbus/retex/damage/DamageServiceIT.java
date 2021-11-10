package com.airbus.retex.damage;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.damage.DamageFullDto;
import com.airbus.retex.business.dto.damage.DamageLightDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.persistence.routingComponent.RoutingComponentRepository;
import com.airbus.retex.service.damage.IDamageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DamageServiceIT extends BaseControllerTest {

    @Autowired
    private IDamageService damageService;

    @Autowired
    private RoutingComponentRepository routingComponentRepository;

    @BeforeEach
    public void before() {

    }

    @Test
    public void revokeDamageAlreadyLinkedToRC() {
        assertThrows(FunctionalException.class, () ->
            damageService.deleteDamage(dataset.damage_corrosion.getNaturalId())
        );
    }

    @Test
    public void shouldReturnIsDeletableOnGetAll() {
        List<DamageLightDto> damageLightDtos = damageService.getAllDamages(Language.FR, EnumActiveState.ACTIVE);
        damageLightDtos.forEach(damageLightDto -> {
            if (dataset.damage_corrosion.getTechnicalId().equals(damageLightDto.getTechnicalId())) {
                assertThat(damageLightDto.isDeletable(), equalTo(false));
            } else {
                assertThat(damageLightDto.isDeletable(), equalTo(true));
            }
        });
    }

    @Test
    public void shouldReturnIsDeletableOnFunctionality() throws NotFoundException {
        DamageFullDto damageFullDto = damageService.getDamage(dataset.damage_corrosion.getNaturalId(), null);
        damageFullDto.getAffectedFunctionalities().forEach((language, functionalityItemDto )-> {
            functionalityItemDto.forEach(functionalityItemDto1 -> {
                if(routingComponentRepository.existsByDamageIdAndFunctionalityId(dataset.damage_corrosion.getNaturalId(), functionalityItemDto1.getId())) {
                    assertThat(functionalityItemDto1.isDeletable(), equalTo(false));
                } else {
                    assertThat(functionalityItemDto1.isDeletable(), equalTo(true));
                }
            });

        });
    }

    @Test
    public void shouldReturnIsNotDeletableOnFunctionality() throws NotFoundException {
        dataSetInitializer.createFunctionalityDamage(dataset.functionality_teeth, dataset.damage_corrosion);
        dataSetInitializer.createRoutingComponent(routingComponent1 -> {
            routingComponent1.setFunctionality(dataset.functionality_teeth);
            routingComponent1.setDamage(dataset.damage_corrosion);
        });

        DamageFullDto damageFullDto = damageService.getDamage(dataset.damage_corrosion.getNaturalId(), null);
        damageFullDto.getAffectedFunctionalities().forEach((language, functionalityItemDto )-> {
            functionalityItemDto.forEach(functionalityItemDto1 -> {
                if(routingComponentRepository.existsByDamageIdAndFunctionalityId(dataset.damage_corrosion.getNaturalId(), functionalityItemDto1.getId())) {
                    assertThat(functionalityItemDto1.isDeletable(), equalTo(false));
                } else {
                    assertThat(functionalityItemDto1.isDeletable(), equalTo(true));
                }
            });

        });
    }

}
