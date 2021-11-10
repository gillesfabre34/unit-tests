package com.airbus.retex.service.impl.part.mapper;

import static com.airbus.retex.business.mapper.MapperUtils.updateList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.airbus.retex.business.dto.functionalArea.FunctionalAreaCreateOrUpdateDto;
import com.airbus.retex.business.dto.part.PartCreateUpdateFunctionalAreaDto;
import com.airbus.retex.business.dto.part.PartCreationDto;
import com.airbus.retex.business.dto.part.PartDto;
import com.airbus.retex.business.dto.part.PartUpdateHeaderDto;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.business.mapper.ReferenceMapper;
import com.airbus.retex.model.basic.AncestorContext;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.part.Mpn;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.persistence.childRequest.PhysicalPartRepository;
import com.airbus.retex.persistence.mpn.MpnRepository;
import com.airbus.retex.persistence.routing.RoutingRepository;

@Mapper(componentModel = "spring", uses = {ReferenceMapper.class})
public abstract class PartMapper extends AbstractMapper {

    @Autowired
    private MpnRepository mpnRepository;
    @Autowired
    private RoutingRepository routingRepository;
    @Autowired
    private PhysicalPartRepository physicalPartRepository;

    /* -----MAPPING PART ----- */

    @Mapping(source = "ataCode", target = "ata")
    @Mapping(source = "partDesignationId", target = "partDesignation")
    public abstract void updatePart(PartCreateUpdateFunctionalAreaDto source, @MappingTarget Part target, @Context AncestorContext context);

    @Mapping(source = "ataCode", target = "ata")
    @Mapping(source = "partDesignationId", target = "partDesignation")
    public abstract void updatePart(PartUpdateHeaderDto source, @MappingTarget Part target, @Context AncestorContext context);

    @Mapping(source = "ataCode", target = "ata")
    @Mapping(source = "partDesignationId", target = "partDesignation")
    public abstract void updatePart(PartCreationDto source, @MappingTarget Part target, @Context AncestorContext context);

    /* -----MAPPING MPN ----- */

    public void updateMpnCodes(Collection<String> sourceList, @MappingTarget Collection<Mpn> targetList, @Context AncestorContext context){
		BiFunction<String, Mpn, Boolean> isSame = (source, target) -> target.getCode().equals(source);
		Function<String, Mpn> resolveDestinationFromSource = source -> {
            Optional<Mpn> mpnOpt = mpnRepository.findByCode(source);
            return mpnOpt.orElseGet(() -> mpnRepository.save(new Mpn(source)));
        };
        updateList(sourceList, targetList, isSame, resolveDestinationFromSource);
    }

    /* -----MAPPING FUNCTIONAL AREA ----- */

    public void updateFunctionalAreaList(Collection<FunctionalAreaCreateOrUpdateDto> sourceList, @MappingTarget Collection<FunctionalArea> targetList, @Context AncestorContext context){
        BiFunction<FunctionalAreaCreateOrUpdateDto, FunctionalArea, Boolean> isSame = (source, target) -> {
            if (null == target.getNaturalId()) {
                return false;
            }
            return target.getNaturalId().equals(source.getNaturalId());
        };

        Supplier<FunctionalArea> newDestinationInstance = FunctionalArea::new;
        BiConsumer<FunctionalAreaCreateOrUpdateDto, FunctionalArea> mapSourceToDestination = (source, target) -> {
            updateFunctionalArea(source, target, context);
            target.setPart(context.findCloneAncestor(Part.class));
        };
        updateList(sourceList, targetList, isSame, newDestinationInstance, mapSourceToDestination);
    }

    @Mapping(source = "faNameId", target = "functionalAreaName")
    @Mapping(source = "functionalityId", target = "functionality")
    @Mapping(source = "treatmentId", target = "treatment")
    public abstract void updateFunctionalArea(FunctionalAreaCreateOrUpdateDto source, @MappingTarget FunctionalArea target, @Context AncestorContext context);

    public abstract List<PartDto> createPartDtoList(Collection<Part> sourceList);

    @Mapping(source = "versionNumber", target = "versionNumber")
    public abstract void toPartDto(Part sourceList, @MappingTarget PartDto target);

    @AfterMapping
    public void afterUpdate(Part source, @MappingTarget PartDto target) {
        target.setDeletable(!(routingRepository.existsByPartTechnicalId(source.getTechnicalId()) || physicalPartRepository.existsByPart(source)));
    }
}
