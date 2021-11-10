package com.airbus.retex.business.dto.damage;

import com.airbus.retex.business.dto.functionality.FunctionalityItemDto;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.damage.DamageFieldsEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DamageFullDto extends DamageDto {
    private Map<Language, Map<DamageFieldsEnum, String>> translatedFields;
    private Map<Language, List<FunctionalityItemDto>> affectedFunctionalities;

    private Boolean isLatestVersion;
    private Long versionNumber;
    private EnumStatus status;
    private LocalDateTime creationDate;
}

