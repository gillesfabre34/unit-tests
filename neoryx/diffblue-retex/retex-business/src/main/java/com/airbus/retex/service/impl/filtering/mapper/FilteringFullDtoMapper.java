package com.airbus.retex.service.impl.filtering.mapper;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.airbus.retex.business.dto.filtering.FilteringFullDto;
import com.airbus.retex.business.dto.mpn.MpnDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.part.Mpn;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.service.filtering.IRetrieveChildRequestService;
import com.airbus.retex.service.translate.ITranslateService;


@Mapper(componentModel = "spring")
public abstract class FilteringFullDtoMapper {
    @Autowired
    private ITranslateService translateService;

    @Autowired
    private IRetrieveChildRequestService retrieveChildRequestService;

	@Mapping(source = "physicalPart.part.partNumber", target = "partNumber")
	@Mapping(source = "physicalPart.serialNumber", target = "serialNumber")
	@Mapping(source = "physicalPart.equipmentNumber", target = "equipmentNumber")
	@Mapping(source = "physicalPart.part.partDesignation", target = "designation")
	@Mapping(source = "physicalPart.part.media", target = "partMedia")
	@Mapping(source = "drt.childRequest.parentRequest.origin", target = "origin")
	@Mapping(source = "drt.id", target = "drtId")
	@Mapping(source = "drt.integrationDate", target = "integrationDate")
	@Mapping(source = "medias", target = "medias")
    public abstract FilteringFullDto convert(Filtering filtering, @Context Language language) throws FunctionalException;

    protected abstract List<UUID> mediasToMediaUuids(Set<Media> medias);

    protected UUID mediasToMediaUuid(Media media) {
        return media.getUuid();
    }


    @AfterMapping
    public void mapAdditionalValues(Filtering filtering, @MappingTarget FilteringFullDto filteringFullDto, @Context Language language) throws FunctionalException {
        if (filtering.getDrt() != null && filtering.getDrt().getChildRequest() != null) {
            filteringFullDto.setAssociatedRequest(translateService.getFieldValue(Request.class.getSimpleName(),
                    filtering.getDrt().getChildRequest().getParentRequest().getId(), Request.FIELD_NAME, language));
        }
        if (filtering.getPhysicalPart().getPart() != null){
            MpnDto mpn = new MpnDto();
            Set<Mpn> npms = filtering.getPhysicalPart().getPart().getMpnCodes();
            if (!npms.isEmpty()) {
                mpn.setCode(npms.iterator().next().getCode());
                filteringFullDto.setMpn(mpn);
            }
        }
        filteringFullDto.setCanCreateDrt(retrieveChildRequestService.getChildRequest(filteringFullDto.getPartNumber(),
                filteringFullDto.getSerialNumber()).isPresent());
    }
}
