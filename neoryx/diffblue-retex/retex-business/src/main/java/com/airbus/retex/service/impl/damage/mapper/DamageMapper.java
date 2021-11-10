package com.airbus.retex.service.impl.damage.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.airbus.retex.business.dto.damage.DamageCreationDto;
import com.airbus.retex.business.dto.damage.DamageFullDto;
import com.airbus.retex.business.dto.damage.DamageLightDto;
import com.airbus.retex.business.dto.damage.functionality.FunctionalityDamageDto;
import com.airbus.retex.business.dto.functionality.FunctionalityFieldsEnum;
import com.airbus.retex.business.dto.functionality.FunctionalityItemDto;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.business.mapper.ReferenceMapper;
import com.airbus.retex.model.basic.AncestorContext;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.model.functionality.Functionality;
import com.airbus.retex.model.functionality.damage.FunctionalityDamage;
import com.airbus.retex.persistence.damage.functionality.FunctionalityDamageRepository;
import com.airbus.retex.persistence.routingComponent.RoutingComponentRepository;
import com.airbus.retex.service.impl.translate.TranslationMapper;
import com.airbus.retex.service.translate.ITranslateService;

@Mapper(componentModel = "spring", uses = {ReferenceMapper.class, TranslationMapper.class})
public abstract class DamageMapper extends AbstractMapper {

    @Autowired
    FunctionalityDamageRepository functionalityDamageRepository;
    @Autowired
    TranslationMapper translationMapper;
    @Autowired
    ITranslateService translateService;
    @Autowired
    RoutingComponentRepository routingComponentRepository;

    /* -----MAPPING DAMAGE ----- */

    public abstract void updateDamage(DamageCreationDto source, @MappingTarget Damage target, @Context AncestorContext context);

    /* -----MAPPING TO DTO ----- */

    @Mapping(source = "translations", target = "translatedFields")
    @Mapping(source = "source.functionalityDamages" , target = "affectedFunctionalities")
    public abstract DamageFullDto createDamageFullDto(Damage source);

    public abstract List<FunctionalityDamageDto> createListFunctionalityDamageDto(Collection<FunctionalityDamage> source);

    @Mapping(source = "translations", target = "translatedFields")
    public abstract FunctionalityDamageDto createFunctionalityDamageDto(FunctionalityDamage source);

    public abstract List<DamageLightDto> createDamageLightDto(List<Damage> damages);

    @AfterMapping
    protected void isDamageDeletable(Damage source, @MappingTarget DamageLightDto target) {
        if (!source.getStatus().equals(EnumStatus.CREATED)
            && routingComponentRepository.existsByDamageId(source.getNaturalId())) {
            target.setDeletable(Boolean.FALSE);
        } else {
            target.setDeletable(Boolean.TRUE);
        }
    }

    /**
     * Get damage affected functionalities
     *
     * @return Map<Language, List<FunctionalityItemDto>>
     */
    protected Map<Language, List<FunctionalityItemDto>> getAffectedFunctionalitiesOfDamage(Collection<FunctionalityDamage> functionalityDamages) {
        List<Functionality> affectedFunctionalities = new ArrayList<>();
		EnumMap<Language, List<FunctionalityItemDto>> result = new EnumMap<>(Language.class);
        if(null != functionalityDamages) {
            functionalityDamages.forEach(fd -> affectedFunctionalities.add(fd.getFunctionality()));
            for (Language lang : Language.values()) {
                List<FunctionalityItemDto> functionalityItemDtoList = new ArrayList<>();
                affectedFunctionalities.stream().distinct().forEach(functionality -> {
                    FunctionalityItemDto functionalityItemDto = new FunctionalityItemDto();
                    functionalityItemDto.setId(functionality.getId());
					functionalityItemDto.setName(
                            translateService.getFieldValue(functionality, FunctionalityFieldsEnum.name.name(), lang));
                    functionalityItemDto.setDeletable(!routingComponentRepository.existsByDamageIdAndFunctionalityId(functionalityDamages.iterator().next().getDamage().getNaturalId(), functionality.getId()));
                    functionalityItemDtoList.add(functionalityItemDto);
                });
                // sort FunctionalityItem by name
                functionalityItemDtoList.sort(Comparator.comparing(FunctionalityItemDto::getName));
                result.put(lang, functionalityItemDtoList);
            }
        }
        return result;
    }

}
