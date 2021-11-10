package com.airbus.retex.business.dto.request;

import com.airbus.retex.business.annotation.RequireTranslation;
import com.airbus.retex.business.dto.CreationDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.request.RequestFieldsEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;


@Data
@NoArgsConstructor
public class RequestCreationDto implements CreationDto {
    
    @NotNull
    @RequireTranslation(all = false, fields = {"name"})
    private Map<Language, Map<RequestFieldsEnum, String>> translatedFields;

    @NotNull
    private Long airbusEntityId;

    @FutureOrPresent(message = "{retex.error.request.dueDate.invalid}")
    private LocalDate dueDate;
}
