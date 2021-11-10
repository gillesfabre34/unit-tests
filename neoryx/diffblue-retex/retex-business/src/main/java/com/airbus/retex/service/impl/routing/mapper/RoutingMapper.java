package com.airbus.retex.service.impl.routing.mapper;

import java.util.EnumSet;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.business.dto.operationPost.RoutingFunctionalAreaPostLightDto;
import com.airbus.retex.business.dto.part.PartDesignationFullDto;
import com.airbus.retex.business.dto.part.PartLightDesignationFullDto;
import com.airbus.retex.business.dto.post.PostCustomDto;
import com.airbus.retex.business.dto.routing.RoutingDto;
import com.airbus.retex.business.dto.routing.RoutingFullDto;
import com.airbus.retex.business.dto.step.StepActivationDto;
import com.airbus.retex.business.dto.step.StepCustomDto;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.business.mapper.ReferenceMapper;
import com.airbus.retex.model.basic.AncestorContext;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.part.PartDesignation;
import com.airbus.retex.model.part.PartDesignationFieldsEnum;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.step.StepActivation;
import com.airbus.retex.service.impl.translate.TranslationMapper;
import com.airbus.retex.service.translate.ITranslateService;

@Mapper(componentModel = "spring", uses = {ReferenceMapper.class, TranslationMapper.class})
public abstract class RoutingMapper extends AbstractMapper {

    @Autowired
    private ITranslateService translateService;

    @Mapping(source = "versionNumber", target = "versionNumber")
    @Mapping(source = "isLatestVersion", target = "isLatestVersion")
    @Mapping(source = "translations", target = "translatedFields")
    public abstract void toFullDto(Routing routing, @MappingTarget RoutingFullDto dto, @Context AncestorContext context);

    @Mapping(source = "versionNumber", target = "versionNumber")
    @Mapping(source = "isLatestVersion", target = "isLatestVersion")
    public abstract void toDto(Routing routing, @MappingTarget RoutingDto dto, @Context AncestorContext context);

    public abstract void toPartLightDesignationFullDto(Part part, @MappingTarget PartLightDesignationFullDto dto, @Context AncestorContext context);

    public abstract void toPartDesignationDto(PartDesignation partDesignation, @MappingTarget PartDesignationFullDto dto, @Context AncestorContext context);

    public abstract void toMediaDto(Media media, @MappingTarget MediaDto dto, @Context AncestorContext context);

    public void toListDto(List<Routing> routings, @MappingTarget List<RoutingDto> dtos, @Context AncestorContext context) {
        routings.forEach(routing -> {
            RoutingDto dto = new RoutingDto();
            toDto(routing, dto, context);
            dtos.add(dto);
        });
    }

    @AfterMapping
    public void setPartDesignationTranslations(Routing routing, @MappingTarget RoutingFullDto dto, @Context AncestorContext context) {
        dto.getPart()
            .getPartDesignation()
            .setTranslatedFields(translateService.getTranslatedFields(routing.getPart().getPartDesignation(), EnumSet.allOf(PartDesignationFieldsEnum.class)));
    }




    public abstract StepActivationDto convertCustomStepActivationDto(StepActivation stepActivation);

	@Mapping(source = "step.stepNumber", target = "stepNumber")
	@Mapping(source = "step.files", target = "files")
	@Mapping(source = "step.type", target = "type")
    public abstract StepCustomDto convertCustomStepDto(Step step, @MappingTarget StepCustomDto stepCustomDto);


	@Mapping(source = "post.measureUnit", target = "measureUnit")
    public abstract PostCustomDto convertCustomPostDto(Post post, @MappingTarget PostCustomDto postCustomDto);

    public abstract RoutingFunctionalAreaPostLightDto convertRoutingFAPostDto(RoutingFunctionalAreaPost routingFunctionalAreaPost);
}
