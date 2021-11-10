package com.airbus.retex.business.dto.mission;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.mission.MissionType;
import com.airbus.retex.model.mission.MissionTypeFieldsEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MissionTypeLightDto extends TranslatedDto {

    private Long id;

    private final TranslateDto name = new TranslateDto(this, MissionTypeFieldsEnum.name);

    @Override
    public String getClassName() {
        return MissionType.class.getSimpleName();
    }

    @Override
    public Long getEntityId() {
        return id;
    }

}

