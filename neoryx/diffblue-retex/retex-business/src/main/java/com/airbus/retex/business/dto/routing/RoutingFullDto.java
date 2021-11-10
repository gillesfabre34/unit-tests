package com.airbus.retex.business.dto.routing;

import com.airbus.retex.business.dto.IVersionableOutDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.business.dto.part.PartLightDesignationFullDto;
import com.airbus.retex.business.dto.user.UserLightDto;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routing.RoutingFieldsEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;


@Getter
@Setter
public class RoutingFullDto extends TranslatedDto implements IVersionableOutDto {

    private Long id;
    private Long versionNumber;
    private Boolean isLatestVersion;
    private LocalDateTime creationDate;
    private EnumStatus status;
    private PartLightDesignationFullDto part;
    private Map<Language, Map<RoutingFieldsEnum, String>> translatedFields;
    private UserLightDto creator;

    @JsonIgnore
    private Long technicalId;

    @Override
    public String getClassName() {
        return Routing.class.getSimpleName();
    }

    @Override
    public Long getEntityId() {
        return technicalId;
    }

    @Override
    public void setNaturalId(Long naturalId) {
        this.id = naturalId;
    }
}
